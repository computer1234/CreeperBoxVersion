package helper.creeperbox.feature.settings;

import java.util.function.BooleanSupplier;

import helper.creeperbox.feature.module.Module;

public class BasicValue<T> {


    protected String name;
    public BooleanSupplier hideIf;

    protected T currentValue;

    protected T defaultValue;
    protected Change<T> onValueChange;

    protected Module parent;
    public BasicValue(String name,Module parent,BooleanSupplier hideIf, T currentValue, T defaultValue, Change<T> onValueChange){
        this.hideIf = hideIf;
        this.name = name;
        this.defaultValue = defaultValue;
        this.onValueChange = onValueChange;
        setValue(currentValue);
        if(parent != null) parent.getSettings().add(this);
        this.parent = parent;
    }



    public BasicValue(String name,Module parent,T defaultValue,T currentValue){
        this.name = name;
        this.defaultValue = defaultValue;
        setValue(currentValue);
        if(parent != null) parent.getSettings().add(this);
        this.parent = parent;
    }

    public BasicValue(String name,Module parent,T defaultValue){
        this.name = name;
        this.defaultValue = defaultValue;
        setValue(defaultValue);
        if(parent != null) parent.getSettings().add(this);
        this.parent = parent;
    }


    public BasicValue(String name,Module parent,T defaultValue,BooleanSupplier hideIf){
        this.name = name;
        this.defaultValue = defaultValue;
        this.hideIf = hideIf;
        setValue(defaultValue);
        if(parent != null) parent.getSettings().add(this);
        this.parent = parent;
    }


    public boolean isHide(){
        return hideIf != null && hideIf.getAsBoolean();
    }
    public BasicValue(String name,Module parent,T defaultValue,T currentValue,BooleanSupplier hideIf){
        this.name = name;
        this.defaultValue = defaultValue;
        this.hideIf = hideIf;
        setValue(currentValue);
        if(parent != null) parent.getSettings().add(this);
        this.parent = parent;
    }

    public BasicValue(String name,Module parent,T currentValue,Change<T> onValueChange){
        this.name = name;
        this.onValueChange = onValueChange;
        setValue(currentValue);
        if(parent != null) parent.getSettings().add(this);
        this.parent = parent;
    }



    public void setValue(T value){
        this.currentValue = this.onValueChange!=null?this.onValueChange.onChange(value):value;
    }

    public String getName(){
        return name;
    }

    public T getCurrentValue() {
        return currentValue;
    }


    public T getDefaultValue() {
        return defaultValue;
    }

    public void setHideIf(BooleanSupplier hideIf) {
        this.hideIf = hideIf;
    }

    public void setOnValueChange(Change<T> onValueChange) {
        this.onValueChange = onValueChange;
    }

}
