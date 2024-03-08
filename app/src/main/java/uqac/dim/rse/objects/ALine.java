package uqac.dim.rse.objects;

import android.widget.ImageView;

import java.util.HashMap;

public abstract class ALine {
    public int id;
    public String shortName;
    public String name;
    public String color;
    public HashMap<String, LineRoute> routes = new HashMap<>();
    public HashMap<String, Picto> pictos = new HashMap<>();

    public ImageView pictoImageView;
}
