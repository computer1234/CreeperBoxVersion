package helper.creeperbox.feature.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import helper.creeperbox.feature.module.Module;


public class ListValue extends BasicValue<String> {

    private int index;
    private List<String> subLists;


    public ListValue(String name, Module parent, BooleanSupplier hideIf, String currentValue, String defaultValue, Change<String> onValueChange) {
        super(name,parent, hideIf, currentValue, defaultValue, onValueChange);
        subLists = new ArrayList<>();
    }

    public ListValue addSubList(String name){
        subLists.add(name);
        setValue(defaultValue);
        return this;
    }


    @Override
    public void setValue(String value) {
        int index = getValueIndex(value);
        if(index == -1){
            return;
        }
        this.index = index;
        super.setValue(value);
    }

    @Override
    public String getCurrentValue() {
        if(index == -1){
            return null;
        }
        return subLists.get(index);
    }


    public int getIndex() {
        return index;
    }


    public List<String> getSubLists() {
        return subLists;
    }

    public int getValueIndex(String name){
        if(subLists == null){
            return -1;
        }
        for(int i = 0; i < subLists.size(); i++){
            String sub = subLists.get(i);
            if(sub.equals(name)){
                return i;
            }
        }
        return -1;
    }

    public ListValue(String name, Module parent, String defaultValue, String currentValue) {
        super(name,parent, defaultValue, currentValue);
        subLists = new ArrayList<>();
    }

    public ListValue(String name,Module parent, String defaultValue) {
        super(name,parent,defaultValue);
        subLists = new ArrayList<>();
    }

    public ListValue(String name, Module parent, String defaultValue, BooleanSupplier hideIf) {
        super(name,parent, defaultValue, hideIf);
        subLists = new ArrayList<>();
    }

    public ListValue(String name, Module parent, String defaultValue, String currentValue, BooleanSupplier hideIf) {
        super(name,parent, defaultValue, currentValue, hideIf);
        subLists = new ArrayList<>();
    }



}

