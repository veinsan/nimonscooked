package com.nimonscooked.model.menu;

public class MenuManager {

    private MenuState state = MenuState.MAIN_MENU;
    private StageOption selectedStage;

    public MenuState getState() {
        return state;
    }

    public StageOption getSelectedStage() {
        return selectedStage;
    }

 

    public void handleMainMenuSelection(MainMenuOption option) {
        switch (option) {
            case START_GAME -> state = MenuState.STAGE_SELECT;
            case HOW_TO_PLAY -> state = MenuState.HOW_TO_PLAY;
            case EXIT -> state = MenuState.EXIT;
        }
    }

  
    public void handleStageSelection(int index) {

        if (index == 1) {
            selectedStage = StageOption.BURGER_C;
            state = MenuState.MAIN_MENU;
        } else {
            throw new IllegalArgumentException("Stage index tidak valid: " + index);
        }
    }

    public void backToMainMenu() {
        state = MenuState.MAIN_MENU;
    }
}
