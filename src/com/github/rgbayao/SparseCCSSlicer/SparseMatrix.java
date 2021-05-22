package com.github.rgbayao.SparseCCSSlicer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * SparseMatrix in CCS (Compressed Column Storage) format
 * ap: list of column pointers
 * ai: list of row indexes
 * ax: list of values
 * More information: https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_column_(CSC_or_CCS)
 */
public class SparseMatrix implements Cloneable {
    private int[] ap;
    private int[] ai;
    private double[] ax;

    public SparseMatrix(int[] ap, int[] ai, double[] ax) {
        this.ap = ap;
        this.ai = ai;
        this.ax = ax;
    }

    public SparseMatrix delCol(int[] columns) {
        if (columns.length == 0) {
            return (SparseMatrix) this.clone();
        }

        Arrays.sort(columns);

        List<Integer> colpointer = new LinkedList<>();
        List<Integer> rowpointer = new LinkedList<>();
        List<Double> values = new LinkedList<>();

        int aux = 0;
        int aux2 = 0;
        int cache = 0;
        int it;

        for (int i : columns) {

            for (it = aux; it < ap[i]; it++) {
                values.add(ax[it]);
                rowpointer.add(ai[it]);
            }

            aux = ap[i + 1];

            for (it = aux2; it < i; it++) {
                colpointer.add(ap[it] - cache);
            }

            cache = cache + ap[i + 1] - ap[i];
            aux2 = it + 1;
        }
        if (columns[columns.length - 1] < ap.length - 2) {
            for (it = aux; it < ap[ap.length - 1]; it++) {
                values.add(ax[it]);
                rowpointer.add(ai[it]);
            }
            for (it = aux2; it < ap.length - 1; it++) {
                colpointer.add(ap[it] - cache);
            }
        }

        colpointer.add(values.size());

        int[] newAp = new int[colpointer.size()];
        Object[] colpointerArr = colpointer.toArray();

        for (int i = 0; i < newAp.length; i++) {
            newAp[i] = (int) colpointerArr[i];
        }

        double[] newAx = new double[values.size()];
        Object[] valuesArr = values.toArray();
        int[] newAi = new int[rowpointer.size()];
        Object[] rowpointerArr = rowpointer.toArray();

        for (int i = 0; i < newAx.length; i++) {
            newAx[i] = (double) valuesArr[i];
            newAi[i] = (int) rowpointerArr[i];
        }

        return new SparseMatrix(newAp,newAi,newAx);
    }

    public SparseMatrix delRow(int[] rows) {

        if (rows.length == 0) {
            return (SparseMatrix) this.clone();
        }

        Arrays.sort(rows);

        List<Integer> colpointer = new LinkedList<>();
        List<Integer> rowpointer = new ArrayList<>();
        List<Double> values = new LinkedList<>();

        int cache = 0;

        for (int i = 1; i < ap.length; i++) {
            colpointer.add(ap[i - 1] - cache);
            for (int j = ap[i - 1]; j < ap[i]; j++) {
                if (!listContains(rows, ai[j])) {
                    rowpointer.add(ai[j]);
                    values.add(ax[j]);
                } else {
                    cache++;
                }
            }
        }
        colpointer.add(values.size());

        cache = 0;
        for (int i : rows) {
            for (int j = 0; j < rowpointer.size(); j++) {
                if (i - cache < rowpointer.get(j)) {
                    rowpointer.set(j, rowpointer.get(j) - 1);
                }
            }
            cache++;
        }

        int[] newAp = new int[colpointer.size()];
        Object[] colpointerArr = colpointer.toArray();

        for (int i = 0; i < newAp.length; i++) {
            newAp[i] = (int) colpointerArr[i];
        }

        double[] newAx = new double[values.size()];
        Object[] valuesArr = values.toArray();
        int[] newAi = new int[rowpointer.size()];
        Object[] rowpointerArr = rowpointer.toArray();

        for (int i = 0; i < newAx.length; i++) {
            newAx[i] = (double) valuesArr[i];
            newAi[i] = (int) rowpointerArr[i];
        }

        SparseMatrix slicedMatrix = new SparseMatrix(newAp,newAi,newAx);

        return slicedMatrix;
    }

    private boolean listContains(int[] list, int item) {
        for (int i : list) {
            if (i == item) {
                return true;
            }
        }
        return false;
    }

    public int[] getAp() {
        return ap;
    }

    public void setAp(int[] ap) {
        this.ap = ap;
    }

    public int[] getAi() {
        return ai;
    }

    public void setAi(int[] ai) {
        this.ai = ai;
    }

    public double[] getAx() {
        return ax;
    }

    public void setAx(double[] ax) {
        this.ax = ax;
    }

    @Override
    public Object clone() {
        return new SparseMatrix(ap, ai, ax);
    }
}
