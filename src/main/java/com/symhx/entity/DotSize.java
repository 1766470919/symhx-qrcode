package com.symhx.entity;

import java.util.Objects;

/**
 * @author lj
 * @date 2020/12/11
 */
public class DotSize {
    public static final DotSize SIZE_1_1 = new DotSize(1, 1);
    public static final DotSize SIZE_2_1 = new DotSize(2, 1);
    public static final DotSize SIZE_1_2 = new DotSize(1, 2);
    public static final DotSize SIZE_2_2 = new DotSize(2, 2);
    private int row;
    private int col;

    public int size() {
        return this.row * this.col;
    }

    public static DotSize create(int row, int col) {
        if (row == 1 && col == 1) {
            return SIZE_1_1;
        } else if (row == 2 && col == 1) {
            return SIZE_2_1;
        } else if (row == 1 && col == 2) {
            return SIZE_1_2;
        } else {
            return row == 2 && col == 2 ? SIZE_2_2 : new DotSize(row, col);
        }
    }

    public DotSize() {
    }

    public DotSize(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            DotSize dotSize = (DotSize)o;
            return this.row == dotSize.row && this.col == dotSize.col;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{this.row, this.col});
    }

    @Override
    public String toString() {
        return "DotSize{row=" + this.row + ", col=" + this.col + '}';
    }
}
