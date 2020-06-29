package LabWork;

import java.io.Serializable;
/**
 * @author Кирилл Сокоркин R3137
 * В данном ENUM находятся все возможные варианты сложности Лабораторных работ
 */
public enum Difficulty implements Serializable {
    NORMAL,
    HARD,
    INSANE,
    HOPELESS,
    TERRIBLE;
}