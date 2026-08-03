package helper.creeperbox.feature.settings;

import helper.creeperbox.feature.module.Module;

public class NumberValue extends BasicValue<Number> {

    private Number min;
    private Number max;
    private Number decimalPlaces;

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else return Math.min(value, max);
    }
    public static Number clamp(Number value, Number min, Number max) {
        double doubleValue = value.doubleValue();
        double doubleMin = value.doubleValue();
        if(min != null){
            doubleMin = min.doubleValue();
        }

        double doubleMax = value.doubleValue();
        if (max!=null) {
            doubleMax = max.doubleValue();
        }

        double clampedValue = clamp(doubleValue, doubleMin, doubleMax);

        if (value instanceof Integer) {
            return (int) clampedValue;
        } else if (value instanceof Long) {
            return (long) clampedValue;
        } else if (value instanceof Float) {
            return (float) clampedValue;
        } else {
            return clampedValue;
        }
    }
    @Override
    public void setValue(Number value) {
        if(decimalPlaces == null) {
            currentValue = value;
            return;
        }
        super.setValue(Math.round(clamp(value,min,max).doubleValue() *
                (1.0 / this.decimalPlaces.doubleValue())) /
                (1.0 / this.decimalPlaces.doubleValue()));
    }

    public Number getDecimalPlaces() {
        return decimalPlaces;
    }

    public void forceSet(Number value){
        this.currentValue = Math.round(clamp(value,min,max).doubleValue() *
                (1.0 / this.decimalPlaces.doubleValue())) /
                (1.0 / this.decimalPlaces.doubleValue());
    }


    public NumberValue(String name, Module parent, Number defaultValue, Number min, Number max, Number decimalPlaces) {
        super(name,parent, defaultValue);
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
    }



    public Number getMax() {
        return max;
    }

    public Number getMin() {
        return min;
    }
}
