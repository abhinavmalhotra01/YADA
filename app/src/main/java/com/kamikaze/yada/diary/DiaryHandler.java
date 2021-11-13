package com.kamikaze.yada.diary;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kamikaze.yada.model.Notes;
import com.kamikaze.yada.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiaryHandler {
    User currentUser;
    Context context;

    public DiaryHandler(Context context) {
        this.context=context;
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String ok="";
        if(firebaseUser.getPhotoUrl()!=null) ok=firebaseUser.getPhotoUrl().toString();
        if(currentUser==null) currentUser=new User(firebaseUser.getUid(), firebaseUser.getDisplayName(),ok,new ArrayList<>());
    }

    public void loadData(RecyclerView recyclerView)
    {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot= task.getResult();
                    ArrayList<Diary> diaries= convertToDiary((List<HashMap<String, String>>) documentSnapshot.get("diaries"));
                    currentUser.setDiaries(diaries);
                    recyclerView.setAdapter(new DiaryListRecyclerViewAdapter(context,diaries));
                }
                else
                {
                    Toast.makeText(context, "You failed, failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadData()
    {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot= task.getResult();
                    ArrayList<Diary> diaries= convertToDiary((List<HashMap<String, String>>) documentSnapshot.get("diaries"));
                    currentUser.setDiaries(diaries);
                }
                else
                {
                    Toast.makeText(context, "You failed, failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteDiary(int position,RecyclerView recyclerView)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
//                    currentUser.getDiaries().add(diary);
                    DocumentSnapshot documentSnapshot=task.getResult();
                    ArrayList<Diary> diaries= convertToDiary((List<HashMap<String, String>>) documentSnapshot.get("diaries"));
                    diaries.remove(position);
                    DiaryListRecyclerViewAdapter adapter=new DiaryListRecyclerViewAdapter(context,diaries);
                    recyclerView.setAdapter(adapter);
                    currentUser.setDiaries(diaries);
                    Log.d("Size", String.valueOf(currentUser.getDiaries().size()));
                    documentReference.update("diaries",diaries).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(context, "Diary deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public ArrayList<Diary> getDiaries()
    {
        if(currentUser !=null)
        {
            loadData();
            return currentUser.getDiaries();
        }
        return new ArrayList<>();
    }

    public Diary getDiary(int position)
    {
        if(currentUser!=null && position<currentUser.getDiaries().size()) return getDiaries().get(position);
        return null;
    }

    public void addDiary(Diary diary, RecyclerView recyclerView)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
//                    currentUser.getDiaries().add(diary);
                    DocumentSnapshot documentSnapshot=task.getResult();
                    ArrayList<Diary> diaries= convertToDiary((List<HashMap<String, String>>) documentSnapshot.get("diaries"));
                    diaries.add(diary);
                    currentUser.setDiaries(diaries);
                    DiaryListRecyclerViewAdapter adapter=new DiaryListRecyclerViewAdapter(context,diaries);
                    recyclerView.setAdapter(adapter);
                    Log.d("Size", String.valueOf(currentUser.getDiaries().size()));
                    documentReference.update("diaries",diaries).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(context, "New diary created successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public void updateDiary(int position,String bgImageUrl,RecyclerView recyclerView)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
//                    currentUser.getDiaries().add(diary);
                    DocumentSnapshot documentSnapshot=task.getResult();
                    ArrayList<Diary> diaries= convertToDiary((List<HashMap<String, String>>) documentSnapshot.get("diaries"));
                    Diary item= diaries.get(position);
                    item.setBgImageUrl(bgImageUrl);
                    diaries.set(position,item);
                    currentUser.setDiaries(diaries);
                    DiaryListRecyclerViewAdapter adapter=new DiaryListRecyclerViewAdapter(context,diaries);
                    recyclerView.setAdapter(adapter);
                    Log.d("Size", String.valueOf(currentUser.getDiaries().size()));
                    documentReference.update("diaries",diaries).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.d("Updation","Diary bgImageUrl updated successfully");
                            }
                        }
                    });

                }
            }
        });
    }

    public ArrayList<Diary> convertToDiary(List<HashMap<String,String>> diaryContent)
    {
        ArrayList<Diary> diaries=new ArrayList<>();
        for(int i=0;i<diaryContent.size();i++)
        {
            Notes note=new Notes(diaryContent.get(i).get("note").get("topic"),diaryContent.get(i).get("note").get("desc"),diaryContent.get(i).get("note").get("location"));
            diaries.add(new Diary(diaryContent.get(i).get("title"),diaryContent.get(i).get("description"),diaryContent.get(i).get("location"),diaryContent.get(i).get("bgImageUrl"),note));         }
        return diaries;
    }
}
