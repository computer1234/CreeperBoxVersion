package helper.creeperbox.feature.settings;


import java.util.function.BooleanSupplier;

import helper.creeperbox.feature.module.Module;

public class BooleanValue extends BasicValue<Boolean> {

    private boolean state;

    private boolean defaultState;

    public BooleanValue(String name, Module parent, BooleanSupplier hideIf, Boolean currentValue, Boolean defaultValue, Change<Boolean> onValueChange) {
        super(name,parent, hideIf, currentValue, defaultValue, onValueChange);
    }

    public BooleanValue(String name,Module parent, Boolean defaultValue, Boolean currentValue) {
        super(name,parent, defaultValue, currentValue);
    }

    public BooleanValue(String name, Module parent, Boolean defaultValue) {
        super(name,parent, defaultValue);
    }

    public BooleanValue(String name,Module parent, Boolean defaultValue, BooleanSupplier hideIf) {
        super(name, parent,defaultValue, hideIf);
    }

    public BooleanValue(String name, Module parent, Boolean defaultValue, Boolean currentValue, BooleanSupplier hideIf) {
        super(name, parent,defaultValue, currentValue, hideIf);
    }



}
