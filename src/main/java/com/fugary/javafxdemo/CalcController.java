package com.fugary.javafxdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class CalcController {

    private static final String ADD = "+";
    private static final String MINUS = "-";
    private static final String MULTIPLY = "×";
    private static final String DIVIDE = "÷";
    private static final String DOT = ".";
    private static final String ZERO = "0";
    private static final List<String> TEXT_LIST = new ArrayList<>();
    private static final Stack<String> CALC_STACK = new Stack<>();

    @FXML
    private TextField resultText;

    @FXML
    private Label previewLabel;

    /**
     * 数字按键处理，包括"."
     *
     * @param event
     */
    public void onNumberButtonClicked(ActionEvent event) {
        Button numButton = (Button) event.getSource();
        String text = numButton.getText().trim();
        if (isZeroText() && !text.equals(DOT)) {
            TEXT_LIST.remove(TEXT_LIST.size() - 1);
        }
        if (text.equals(DOT)) {
            if (TEXT_LIST.contains(DOT)) {
                return;
            }
            if (TEXT_LIST.isEmpty()) {
                TEXT_LIST.add(ZERO);
            }
        }
        TEXT_LIST.add(text);
        renderText();
        renderPreviewLabel();
    }

    public void onOperatorButtonClicked(ActionEvent event) {
        Button numButton = (Button) event.getSource();
        String calc = numButton.getText().trim();
        if (!CALC_STACK.isEmpty()) {
            if (!TEXT_LIST.isEmpty()) {
                doCalcResult();
            }
            if (Arrays.asList(ADD, MINUS, MULTIPLY, DIVIDE).contains(CALC_STACK.peek())) { // 运算符替换
                CALC_STACK.pop();
            }
            CALC_STACK.push(calc);
        } else {
            if (!TEXT_LIST.isEmpty()) {
                // 保存
                CALC_STACK.push(getCurrentText());
                CALC_STACK.push(numButton.getText().trim());
                TEXT_LIST.clear();
            }
        }
        renderPreviewLabel();
    }

    /**
     * 退格键处理
     */
    public void onBackButtonClicked() {
        if (!TEXT_LIST.isEmpty()) {
            TEXT_LIST.remove(TEXT_LIST.size() - 1);
        }
        renderText();
    }

    /**
     * 正/负号处理
     */
    public void onSignButtonClicked() {
        if (TEXT_LIST.isEmpty()) {
            TEXT_LIST.add(ZERO);
        }
        if (TEXT_LIST.get(0).equals(MINUS)) {
            TEXT_LIST.remove(0);
        } else {
            TEXT_LIST.add(0, MINUS);
        }
        renderText();
    }

    /**
     * 等号处理
     */
    public void onEqualButtonClicked() {
        if (!TEXT_LIST.isEmpty()) {
            doCalcResult();
        }
    }

    /**
     * 计算结果并显示
     */
    private void doCalcResult() {
        String currentValue = getCurrentText();
        BigDecimal value = new BigDecimal(currentValue);
        renderPreviewLabel();
        while (!CALC_STACK.isEmpty()) {
            String calcSign = CALC_STACK.pop();
            switch (calcSign) {
                case ADD -> {
                    value = new BigDecimal(CALC_STACK.pop()).add(value);
                }
                case MINUS -> {
                    value = new BigDecimal(CALC_STACK.pop()).subtract(value);
                }
                case MULTIPLY -> {
                    value = new BigDecimal(CALC_STACK.pop()).multiply(value);
                }
                case DIVIDE -> {
                    value = new BigDecimal(CALC_STACK.pop()).divide(value, 10, RoundingMode.CEILING);
                }
            }
        }
        clearText();
        CALC_STACK.push(value.toString());
        renderText(value.toString());
        renderPreviewLabel();
    }

    public void onAllClearButtonClicked(ActionEvent event) {
        clearText();
        CALC_STACK.clear();
        renderPreviewLabel();
    }

    /**
     * 是否是0或-0
     *
     * @return
     */
    private boolean isZeroText() {
        if (TEXT_LIST.size() == 1 && TEXT_LIST.get(0).equals(ZERO)) {
            return true;
        }
        return TEXT_LIST.size() == 2 && TEXT_LIST.get(0).equals(MINUS) && TEXT_LIST.get(1).equals(ZERO);
    }

    private void clearText() {
        TEXT_LIST.clear();
        renderText();
    }

    private void renderText() {
        renderText(getCurrentText());
    }

    private void renderText(String value) {
        resultText.setText(value);
        if (value.length() > 12) {
            resultText.setAlignment(Pos.CENTER_LEFT);
        } else {
            resultText.setAlignment(Pos.CENTER_RIGHT);
        }
    }

    /**
     * 预览信息标签处理
     */
    private void renderPreviewLabel() {
        if (TEXT_LIST.isEmpty()) {
            previewLabel.setText(String.join("", CALC_STACK));
        } else if (CALC_STACK.size() != 1) {
            previewLabel.setText(String.join("", CALC_STACK) + getCurrentText());
        } else {
            previewLabel.setText(getCurrentText());
        }
    }

    /**
     * 计算t当前文本框的内容
     *
     * @return
     */
    private String getCurrentText() {
        String result = ZERO;
        if (!TEXT_LIST.isEmpty()) {
            result = String.join("", TEXT_LIST);
        }
        return result;
    }
}
