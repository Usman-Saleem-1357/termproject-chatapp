package com.example.termproject;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

public class DBHelper{
    public DatabaseReference databaseReference;
    FirebaseAuth fAuth;

    public DBHelper()
    {
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
        fAuth = FirebaseAuth.getInstance();

    }

    public void registerUserData(UserModel user)
    {
        databaseReference.child("users").child(user.uid).setValue(user);
    }

    public UserModel getUserData(String uid)
    {
        final UserModel[] userModel = new UserModel[1];
        Task<DataSnapshot> t = databaseReference.child("users").child(uid).get();
        if(t.isSuccessful())
        {
            DataSnapshot data = t.getResult();
            userModel[0] = data.getValue(UserModel.class);
        }
        return userModel[0];
    }

    public FirebaseRecyclerOptions<UserModel> getUserOptions()
    {
        return new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(databaseReference.child("users")
                ,UserModel.class).build();
    }

    public FirebaseRecyclerOptions<RequestModel> getRequestOptions(String uid)
    {
        return new FirebaseRecyclerOptions.Builder<RequestModel>().setQuery(databaseReference.child("Requests").child(getUID())
                ,RequestModel.class).build();
    }

    public Task<Void> sendRequest(RequestModel req)
    {
        return databaseReference.child("Requests").child(getUID()).child("reqid").setValue(req.reqid);
    }

    public Task<AuthResult> loginAuthentication(String username, String password)
    {
        return fAuth.signInWithEmailAndPassword(username, password);
    }

    public Task<AuthResult> registerUser(String username, String password)
    {
        return fAuth.createUserWithEmailAndPassword(username, password);
    }

    public String getUID()
    {
        return fAuth.getCurrentUser().getUid();
    }
}
