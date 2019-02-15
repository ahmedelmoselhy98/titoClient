/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.OrxtraDev.TitoApp.notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String FRIENDLY_ENGAGE_TOPIC = "favor_request";

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */



    //this is for notification
    // firebase
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.d("hazem", "FCM Token: " + token);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        DatabaseReference ref =   mFirebaseDatabaseReference.child("Tokens");




        if (user !=null)
        ref.child(user.getUid()).child("token").setValue(token);

//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference ref =   mFirebaseDatabaseReference.child("Tokens");
//
//        ref.child(user.getUid()).child("token").setValue(token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance()
                .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }
}