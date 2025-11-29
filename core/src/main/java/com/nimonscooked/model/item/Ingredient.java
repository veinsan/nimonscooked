package com.nimonscooked.model.item;

import com.nimonscooked.model.ingredient.Preparable;
import java.util.HashMap;
import java.util.Map;

public class Ingredient extends Item implements Preparable {

    public enum State { 
        RAW("Raw"), 
        CHOPPED("Chopped"), 
        COOKED("Cooked"), 
        BURNT("Burnt"),
        COOKING("Cooking");

        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private State state;
    private String baseTexture;
    
    private static final Map<String, IngredientProperties> INGREDIENT_DATA = new HashMap<>();

    static {
        INGREDIENT_DATA.put("meat", new IngredientProperties(true, true, false));
        INGREDIENT_DATA.put("tomato", new IngredientProperties(true, false, true));
        INGREDIENT_DATA.put("lettuce", new IngredientProperties(true, false, true));
        INGREDIENT_DATA.put("cheese", new IngredientProperties(true, false, true));
        INGREDIENT_DATA.put("bun", new IngredientProperties(false, false, true));
    }

    private static class IngredientProperties {
        final boolean choppable;
        final boolean cookable;
        final boolean rawPlaceable;

        IngredientProperties(boolean choppable, boolean cookable, boolean rawPlaceable) {
            this.choppable = choppable;
            this.cookable = cookable;
            this.rawPlaceable = rawPlaceable;
        }
    }

    public Ingredient(String name, String baseTexture) {
        super(name, baseTexture + ".png");
        this.baseTexture = baseTexture;
        this.state = State.RAW;
        updateTexture();
    }

    public Ingredient(Ingredient other) {
        this(other.getName(), other.baseTexture);
        this.state = other.state;
        updateTexture();
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        if (newState == null) return;
        
        if (!isValidStateTransition(this.state, newState)) {
            return;
        }
        
        this.state = newState;
        updateTexture();
    }

    private boolean isValidStateTransition(State from, State to) {
        if (from == State.BURNT) return false;
        
        switch (to) {
            case CHOPPED:
                return from == State.RAW && canBeChopped();
            case COOKING:
                return (from == State.RAW || from == State.CHOPPED) && canBeCooked();
            case COOKED:
                return from == State.COOKING || (from == State.RAW && canBeCooked());
            case BURNT:
                return from == State.COOKING || from == State.COOKED;
            default:
                return true;
        }
    }

    private void updateTexture() {
        String lowerName = this.name.toLowerCase();
        
        switch (state) {
            case RAW:
                if (lowerName.equals("meat")) {
                    this.textureName = baseTexture + "_raw.png";
                } else {
                    this.textureName = baseTexture + ".png";
                }
                break;
            case CHOPPED:
                this.textureName = baseTexture + "_chopped.png";
                break;
            case COOKING:
            case COOKED:
                this.textureName = baseTexture + "_cooked.png";
                break;
            case BURNT:
                this.textureName = baseTexture + "_burnt.png";
                break;
        }
    }

    @Override
    public boolean canBeChopped() {
        if (state != State.RAW) return false;
        
        IngredientProperties props = INGREDIENT_DATA.get(name.toLowerCase());
        return props != null && props.choppable;
    }

    @Override
    public boolean canBeCooked() {
        if (state == State.COOKED || state == State.BURNT) return false;
        
        IngredientProperties props = INGREDIENT_DATA.get(name.toLowerCase());
        return props != null && props.cookable;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        IngredientProperties props = INGREDIENT_DATA.get(name.toLowerCase());
        if (props == null) return false;
        
        if (state == State.RAW) {
            return props.rawPlaceable;
        }
        
        return state == State.COOKED || state == State.CHOPPED;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            setState(State.CHOPPED);
        }
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            setState(State.COOKED);
        }
    }

    public boolean isBurnt() {
        return state == State.BURNT;
    }

    public boolean isCooking() {
        return state == State.COOKING;
    }

    @Override
    public String getDisplayName() {
        return name + " (" + state.getDisplayName() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ingredient)) return false;
        
        Ingredient other = (Ingredient) obj;
        return name.equalsIgnoreCase(other.name) && state == other.state;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + state.hashCode();
    }
}