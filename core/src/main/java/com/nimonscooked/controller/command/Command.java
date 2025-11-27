package com.nimonscooked.controller.command;

// IMPORT YANG BENAR
import com.nimonscooked.model.entity.Chef;

public interface Command {
    void execute(Chef chef);
}