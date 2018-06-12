package com.example.gunjan.ringtonemanager;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.owlike.genson.Context;
import com.owlike.genson.Deserializer;
import com.owlike.genson.stream.ObjectReader;

import java.lang.reflect.Type;

/**
 * Created by Gunjan on 11-06-18.
 */

public class UriDeserializer implements Deserializer<Uri> {

    @Override
    public Uri deserialize(ObjectReader reader, Context ctx) throws Exception {
        return Uri.parse(reader.valueAsString());

    }
}
