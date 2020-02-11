package com.project.hscef.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.hscef.ComplainRegistration;
import com.project.hscef.ComplainView;
import com.project.hscef.Event_Register;
import com.project.hscef.LoadingActivity;
import com.project.hscef.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private PageViewModel pageViewModel;
    /*public static int l = 0;*/
    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    View layout_adder;
    LinearLayout linearLayout;
    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1 ) {
            return Return_Complaints(inflater,container,savedInstanceState);
            /*if (l==0){l=1;
            layout_adder = inflater.inflate(R.layout.fragment_tabbed, container, false);
            View template = inflater.inflate(R.layout.compeventtemplate, null, false);
            layout_adder.findViewById(R.id.adder_layout);
            linearLayout = layout_adder.findViewById(R.id.adder_layout);
            template = temp_initializer(template, "Demo Title", "Demo Desc", "20-11-2000","URl");
            linearLayout.addView(template);


            db.collection("user_master").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot data = task.getResult();
                    String s = data.getString("SocietyId");
                    Query query = db.collection("complain_master").whereEqualTo("SocietyId",s);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()){
                             for (final QueryDocumentSnapshot document : task.getResult()) {
                                 View template1 = inflater.inflate(R.layout.compeventtemplate, null, false);
                                 template1 = temp_initializer1(document, template1);
                                 linearLayout.addView(template1);
                             }
                         }
                        }
                    });
                }
            });
            return layout_adder;}
            else {
                if ((layout_adder.getParent()) != null){
                l = 0;
                return layout_adder;
            }
                l = 0;
                return layout_adder;
            }*/
        }
        else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2 ) {
            return Return_Events(inflater,container,savedInstanceState);
        }
        else/* if(getArguments().getInt(ARG_SECTION_NUMBER) == 3 ) {*/{
            View root = inflater.inflate(R.layout.action_fragment, container, false);
            Button comp_reg = root.findViewById(R.id.btn_compreg);
            Button event_reg = root.findViewById(R.id.btn_eventreg);
            Button sign_out = root.findViewById(R.id.btn_signout);
            Button leave_soc = root.findViewById(R.id.btn_leavesoc);
            comp_reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ComplainRegistration.class));
                }
            });
            event_reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), Event_Register.class));
                }
            });
            sign_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), LoadingActivity.class));
                    getActivity().finish();
                }
            });
            leave_soc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("user_master").document(user.getUid()).update("SocietyId",null);
                    db.collection("user_master").document(user.getUid()).update("isInSoc","False");
                    startActivity(new Intent(getActivity(), LoadingActivity.class));
                    getActivity().finish();
                }
            });
            return root;
        }
    }
    public View temp_initializer(View template, final String title, String desc, String Dated, String img_src){
        TextView txt_title = template.findViewById(R.id.temp_txt_title);
        TextView txt_desc = template.findViewById(R.id.temp_txt_desc);
        LinearLayout demo = template.findViewById(R.id.demo);
        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), title, Toast.LENGTH_LONG).show();
            }
        });
        ImageView img = template.findViewById(R.id.temp_img);
        txt_title.setText(title);
        txt_desc.setText(desc.replace("\n","-") + "\n" + Dated);
        return template;
    }




    public View Return_Complaints(@NonNull final LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState){
        Log.d("Tab","CALLED1");
        layout_adder = inflater.inflate(R.layout.fragment_tabbed, container, false);
        View template = inflater.inflate(R.layout.compeventtemplate, null, false);
        layout_adder.findViewById(R.id.adder_layout);
        linearLayout = layout_adder.findViewById(R.id.adder_layout);
        template = temp_initializer(template, "Complaints", "Your Complaints will be shown here.", "--","URl");
        linearLayout.addView(template);
        db.collection("user_master").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data = task.getResult();
                String s = data.getString("SocietyId");
                Query query = db.collection("complain_master").whereEqualTo("SocietyId",s);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                View template1 = inflater.inflate(R.layout.compeventtemplate, null, false);
                                template1 = temp_initializer1(document, template1);
                                linearLayout.addView(template1);
                            }
                        }
                    }
                });
            }
        });
        return layout_adder;
    }

    public View Return_Events(@NonNull final LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        Log.d("Tab","CALLED2");
        layout_adder = inflater.inflate(R.layout.fragment_tabbed, container, false);
        View template = inflater.inflate(R.layout.compeventtemplate, null, false);
        layout_adder.findViewById(R.id.adder_layout);
        linearLayout = layout_adder.findViewById(R.id.adder_layout);
        template = temp_initializer(template, "Events", "Your events will be shown here.", "--","URl");
        linearLayout.addView(template);
        db.collection("user_master").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data = task.getResult();
                String s = data.getString("SocietyId");
                Query query = db.collection("event_master").whereEqualTo("SocietyId",s);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                View template1 = inflater.inflate(R.layout.compeventtemplate, null, false);
                                template1 = temp_initializer2(document, template1);
                                linearLayout.addView(template1);
                            }
                        }
                    }
                });
            }
        });

        return layout_adder;
    }


    public View temp_initializer1(final QueryDocumentSnapshot document, View template1){
        TextView txt_title = template1.findViewById(R.id.temp_txt_title);
        TextView txt_desc = template1.findViewById(R.id.temp_txt_desc);
        LinearLayout demo = template1.findViewById(R.id.demo);
        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComplainView.class);
                intent.putExtra("complainid",document.getId());
                intent.putExtra("socid",document.getString("SocietyId"));
                intent.putExtra("type","complain");
                startActivity(intent);
            }
        });
        ImageView img = template1.findViewById(R.id.temp_img);
        txt_title.setText(document.getString("title"));
        txt_desc.setText(document.getString("desc").replace("\n","-") + "\n" + document.getString("problem_date"));
        return template1;
    }
    public View temp_initializer2(final QueryDocumentSnapshot document, View template1){
        TextView txt_title = template1.findViewById(R.id.temp_txt_title);
        TextView txt_desc = template1.findViewById(R.id.temp_txt_desc);
        LinearLayout demo = template1.findViewById(R.id.demo);
        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComplainView.class);
                intent.putExtra("EventId",document.getId());
                intent.putExtra("socid",document.getString("SocietyId"));
                intent.putExtra("type","event");
                startActivity(intent);
            }
        });
        ImageView img = template1.findViewById(R.id.temp_img);
        txt_title.setText(document.getString("title"));
        txt_desc.setText(document.getString("desc").replace("\n","-") + "\n" + document.getString("event_date"));
        return template1;
    }
}