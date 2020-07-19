package com.ecommerce.onlinehut.Admin.deposite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.Transaction;
import com.ecommerce.onlinehut.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class DepositeActivity extends AppCompatActivity {

    private RecyclerView depositeRV;
    
    private ImageButton addButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> users;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private TransactionListAdapter recycleAdapter;
    private Calendar calender;
    EditText animalid, txid, phone, amount, time,animal_id;
    Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposite);

        addButton = findViewById(R.id.addButton);
        depositeRV = findViewById(R.id.depositeRV);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransactionForm();
            }
        });
        getAllUsers();
        get_transaction_history_data();
    }

    private void getAllUsers() {
        users = new ArrayList<>();
        db.collection("Users").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                User user = new User(map.get("user_id") + "", map.get("user_name") + "", map.get("user_type") + "", map.get("phone_number") + "", map.get("image_path") + "", map.get("device_id") + "");
                                user.setAdmin(map.containsKey("admin"));
                                if (map.containsKey("disabled"))
                                    user.setDisabled((Boolean) map.get("disabled"));
                                else user.setDisabled(false);
                                users.add(user);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error getting documents:", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void addTransactionForm() {
        calender = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        Spinner gateway = v.findViewById(R.id.gateway);
        Spinner userid = v.findViewById(R.id.userid);
        animalid = v.findViewById(R.id.animalid);
        txid = v.findViewById(R.id.txid);
        phone = v.findViewById(R.id.phone);
        amount = v.findViewById(R.id.amount);
        animal_id= v.findViewById(R.id.amount);
        time = v.findViewById(R.id.time);
        save = v.findViewById(R.id.add);
        cancel = v.findViewById(R.id.cancel);
        time.setKeyListener(null);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment(calender, time);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        if (users == null)
            addTransactionForm();
        userid.setAdapter(new SpinnerAdapter(this, users));

        builder.setView(v);
        AlertDialog ad = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transaction transaction = new Transaction("", ((User) userid.getSelectedItem()).getUser_name(),
                        ((User) userid.getSelectedItem()).getUser_id(),
                        phone.getText().toString(), Integer.parseInt(amount.getText().toString()),
                        txid.getText().toString(),animal_id.getText().toString(), gateway.getSelectedItem().toString(), time.getText().toString());
                db.collection("Transaction").document().set(transaction).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DepositeActivity.this, "Payment Added", Toast.LENGTH_SHORT).show();
                                    get_transaction_history_data();
                                    ad.dismiss();
                                }
                                else Toast.makeText(DepositeActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
        ad.show();
    }
    public void get_transaction_history_data()
    {
        Query query=db.collection("Transaction");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                QuerySnapshot queryDocumentSnapshots=task.getResult();
                if(queryDocumentSnapshots.size()>0){
                    transactions.clear();
                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                        Map<String,Object> data=documentSnapshot.getData();
                        String image_path=data.get("image_path").toString();
                        String sender_name=data.get("user_name").toString();
                        String sender_id=data.get("user_id").toString();
                        String phone_number=data.get("phone_number").toString();
                        int amount=Integer.parseInt(data.get("amount").toString());
                        String trxId=data.get("transaction_id").toString();
                        String payment_method=data.get("payment_method").toString();
                        String animal_id=data.get("animal_id").toString();
                        String time= data.get("time").toString();
                        transactions.add(new Transaction(image_path,sender_name,sender_id,phone_number,amount,trxId,animal_id,payment_method,time));
                    }
                    initRV(transactions);
                }

            }
        });
    }

    private void initRV(ArrayList<Transaction> transactions) {
        depositeRV.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new TransactionListAdapter(transactions);
        depositeRV.setAdapter(recycleAdapter);
        recycleAdapter.notifyDataSetChanged();
    }

    class SpinnerAdapter extends ArrayAdapter<User> {

        public SpinnerAdapter(@NonNull Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
            if (v == null)
                v = LayoutInflater.from(DepositeActivity.this).inflate(R.layout.user_spinner_row, parent, false);
            TextView name = v.findViewById(R.id.name);
            v.findViewById(R.id.id).setVisibility(View.GONE);
            User u = getItem(position);
            name.setText(u.getUser_name());
            return v;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        public View initView(int position, View v, ViewGroup parent) {
            if (v == null)
                v = LayoutInflater.from(DepositeActivity.this).inflate(R.layout.user_spinner_row, parent, false);
            TextView name = v.findViewById(R.id.name);
            TextView id = v.findViewById(R.id.id);
            User u = getItem(position);
            name.setText(u.getUser_name());
            id.setText(u.getUser_id());
            return v;
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private final EditText time;
        Calendar calendar;
        public TimePickerFragment(Calendar calendar, EditText time) {
            this.calendar = calendar;
            this.time = time;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hourOfDay, minute);
            time.setText(calendar.getTime().toString());
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        Calendar calendar;
        EditText time;
        public DatePickerFragment(Calendar calendar, EditText time) {
            this.calendar = calendar;
            this.time = time;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            TimePickerFragment newFragment = new TimePickerFragment(calendar, time);
            newFragment.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "timepicker");
        }
    }
}

