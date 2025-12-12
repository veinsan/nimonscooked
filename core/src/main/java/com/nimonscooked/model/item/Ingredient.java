package com.nimonscooked.model.item;

import com.nimonscooked.model.ingredient.Preparable;
import java.util.HashMap;
import java.util.Map;

public class Ingredient extends Item implements Preparable {

    public enum State { 
        RAW("Raw"), 
        CHOPPED("Chopped"), 
        COOKING("Cooking"), 
        COOKED("Cooked"), 
        BURNT("Burnt");

        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private State state;
    // baseTexture kita simpan sebagai prefix path (misal: "ingredients/meat")
    private String baseTexturePath; 
    
    private static final Map<String, IngredientProperties> INGREDIENT_DATA = new HashMap<>();

    static {
        // Meat: Bisa dipotong (jadi patty), Bisa dimasak (patty -> burger), Tidak bisa ditaruh piring kalau Raw
        INGREDIENT_DATA.put("meat", new IngredientProperties(true, true, false));
        
        // Tomato, Lettuce, Cheese: Bisa dipotong, Tidak bisa dimasak, Bisa ditaruh piring (Raw/Chopped)
        INGREDIENT_DATA.put("tomato", new IngredientProperties(true, false, true));
        INGREDIENT_DATA.put("lettuce", new IngredientProperties(true, false, true));
        INGREDIENT_DATA.put("cheese", new IngredientProperties(true, false, true));
        
        // Bun: Tidak bisa apa-apa, cuma bisa ditaruh piring
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

    public Ingredient(String name, String texturePath) {
        // Panggil super dengan dummy text dulu, nanti di-update
        super(name, texturePath);
        
        // Normalisasi base path agar logic updateTexture bersih
        // Kita paksa format path jadi: "ingredients/nama"
        this.baseTexturePath = "ingredients/" + name.toLowerCase();
        
        this.state = State.RAW;
        updateTexture(); // Set texture awal yang benar
    }

    // Copy Constructor
    public Ingredient(Ingredient other) {
        super(other.getName(), other.getTextureName());
        this.baseTexturePath = other.baseTexturePath;
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

    /**
     * Mengatur nama file texture berdasarkan State dan Nama Bahan.
     * Sesuai dengan daftar file assets yang tersedia.
     */
    private void updateTexture() {
        String lowerName = this.name.toLowerCase();
        
        switch (state) {
            case RAW:
                // Meat punya suffix "_raw", sisanya polosan (bun.png, tomato.png)
                if (lowerName.equals("meat")) {
                    this.textureName = baseTexturePath + "_raw.png";
                } else {
                    this.textureName = baseTexturePath + ".png";
                }
                break;
                
            case CHOPPED:
                // Semua bahan yang bisa dipotong punya suffix "_chopped"
                this.textureName = baseTexturePath + "_chopped.png";
                break;
                
            case COOKING: // Visual saat masak sama dengan cooked (atau raw tergantung preferensi)
            case COOKED:
                // Hanya meat yang punya suffix "_cooked"
                if (lowerName.equals("meat")) {
                    this.textureName = baseTexturePath + "_cooked.png";
                } else {
                    // Fallback kalau ada bahan lain yang dipaksa masak (misal bug)
                    this.textureName = baseTexturePath + ".png"; 
                }
                break;
                
            case BURNT:
                if (lowerName.equals("meat")) {
                    this.textureName = baseTexturePath + "_burnt.png";
                }
                break;
        }
    }

    private boolean isValidStateTransition(State from, State to) {
        if (from == State.BURNT) return false;
        
        switch (to) {
            case CHOPPED:
                // Hanya bisa dipotong kalau masih RAW
                return from == State.RAW && canBeChopped();
                
            case COOKING:
                // Bisa mulai masak kalau RAW (langsung) atau CHOPPED (patty)
                return (from == State.RAW || from == State.CHOPPED) && canBeCooked();
                
            case COOKED:
                // Selesai masak dari state COOKING
                return from == State.COOKING;
                
            case BURNT:
                // Gosong kalau kelamaan dimasak atau sudah matang tapi ditinggal
                return from == State.COOKING || from == State.COOKED;
                
            default:
                return true;
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
        // Tidak bisa masak kalau sudah matang atau gosong
        if (state == State.COOKED || state == State.BURNT) return false;
        
        // Meat spesial: Harus CHOPPED dulu baru bisa COOKED (jadi patty)
        // Tapi logic ini tergantung game design kamu. 
        // Kalau di Overcooked: Meat -> Chop -> Cook.
        if (name.equalsIgnoreCase("meat") && state == State.RAW) return false;

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
        
        // Bahan Chopped atau Cooked selalu bisa ditaruh piring
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
        // Logic masak biasanya ditangani CookingStation/Thread, 
        // tapi ini helper untuk instant change state
        setState(State.COOKED);
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