package com.example.rajdeeprao.hw_09_a;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rajdeeprao on 4/19/17.
 */

public class UserAdapter extends ArrayAdapter<User> {
    Context context;
    int resource;
    List<User> objects;
    public UserAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resource,parent,false);
        }
        TextView tv1= (TextView) convertView.findViewById(R.id.textView);
        tv1.setText(objects.get(position).getfName()+" "+objects.get(position).getlName());
        ImageView imageView= (ImageView) convertView.findViewById(R.id.imageView);
            Picasso.with(context)
                    .load(objects.get(position).getPhotoURL())
                    .resize(140,140)
                    .into(imageView);
        Button addFriend= (Button) convertView.findViewById(R.id.addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo:","Friend added");
            }
        });

        return convertView;

    }
}