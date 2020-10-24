package com.example.agua_planeta_rica.util;

import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.model.User;
import com.example.agua_planeta_rica.view.main.PlaceholderFragment;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;

public class Common {
    public static FirebaseUser CURRENT_USER;
    public static User CURRENT_USER_MODEL;
    public static String STATE_REQUEST = "Solicitado";
    public static PlaceholderFragment INSTANCE;
    public static DecimalFormat DF = new DecimalFormat("###,###.###");
    public static Request DELIVERY_NOW;
}
