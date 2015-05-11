/*
 * Copyright (c) %today.year Sergio Ceron Figueroa
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL COPYRIGHT HOLDERS OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.underserver.jbigmining.classifiers;

import org.underserver.jbigmining.core.AlgorithmInformation;
import org.underserver.jbigmining.core.Classifier;
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * -
 *
 * @author Gabriel Baltazar P.
 * @version rev: %I%
 * @date 17/04/14 09:45 PM
 */
public class AlfaBetaSVM extends Classifier {
    private DataSet negativeTrainSet;

    private Double[] support;
    private DataSet restrictedSet;

    private Double[] negativeSupport;
    private DataSet negativeRestrictedSet;

    public AlfaBetaSVM() {
        super("AlfaBetaSVM");
    }

    @Override
    public AlgorithmInformation getInformation() {
        AlgorithmInformation result;

        result = new AlgorithmInformation();

        return result;
    }

    private Double[] calculateSupport(DataSet dataSet) {
        Double[] support = new Double[dataSet.getAttributes().size()];

        Double minimum = 2.0, beta, s1, s2;

        for (int i = 0, j; i < support.length; i++) {
            Iterator patterns = dataSet.iterator();

            j = 0;
            while (patterns.hasNext()) {
                s1 = ((Pattern) patterns.next()).toDoubleVector()[i];
                beta = s1;

                if (patterns.hasNext()) {
                    s2 = ((Pattern) patterns.next()).toDoubleVector()[i];
                    beta = (double) Beta(s1.intValue(), s2.intValue());
                }

                minimum = (j == 0) ? beta : Math.min(minimum, beta);

                if (minimum == 0) {
                    break;
                }

                j++;
            }

            support[i] = minimum;
        }

        return support;
    }

    public DataSet restrictSet(DataSet dataSet, Double[] support) {
        DataSet restrictedSet = new DataSet(dataSet);

        Pattern patternT;
        for (Pattern pattern : dataSet) {
            Double[] vector = pattern.toDoubleVector();
            vector = Restriction(support, vector);

            patternT = new Pattern(vector);
            patternT.setClassIndex(pattern.getClassIndex());
            restrictedSet.add(patternT);
        }

        return restrictedSet;
    }

    public DataSet negativeSet(DataSet dataSet) {
        DataSet negativeSet = new DataSet(dataSet);

        Pattern patternT;
        for (Pattern pattern : dataSet) {
            Double[] vector = pattern.toDoubleVector();
            vector = Negation(vector);

            patternT = new Pattern(vector);
            patternT.setClassIndex(pattern.getClassIndex());
            negativeSet.add(patternT);
        }

        return negativeSet;
    }

    @Override
    public void train() {
        DataSet trainSet = getTrainSet();

        support = calculateSupport(trainSet);
        restrictedSet = restrictSet(trainSet, support);

        negativeTrainSet = negativeSet(trainSet);

        negativeSupport = calculateSupport(negativeTrainSet);
        negativeRestrictedSet = restrictSet(negativeTrainSet, negativeSupport);
    }

    public int[] minimumTheta(DataSet dataSet, Pattern pattern) {
        int minimo = 0, theta = 0, value, i = 0;

        for (Pattern patternT: dataSet) {
            value = TransformedTheta(pattern.toDoubleVector(), patternT.toDoubleVector());

            if (i == 0 || value < minimo) {
                minimo = value;
                theta = patternT.getClassIndex();
            }

            i++;
        }

        return new int[]{minimo, theta};
    }

    @Override
    public int classify(Pattern instance) {
        Double[] vector = Restriction(support, instance.toDoubleVector());
        Pattern restrictedInstance = new Pattern(vector);

        int[] omega = minimumTheta(restrictedSet, restrictedInstance);

        vector = Negation(instance.toDoubleVector());
        Pattern negativePattern = new Pattern(vector);

        vector = Restriction(negativeSupport, negativePattern.toDoubleVector());
        Pattern negativeRestrictedInstance = new Pattern(vector);

        int[] phi = minimumTheta(negativeRestrictedSet, negativeRestrictedInstance);

        return (omega[0] <= phi[0]) ? omega[1] : phi[1];
    }

    public Integer Alfa(Integer x, Integer y) {
        int z = 0;
        if (x == 0 && y == 0) {
            z = 1;
        } else if (x == 0 && y == 1) {
            z = 0;
        } else if (x == 1 && y == 0) {
            z = 2;
        } else if (x == 1 && y == 1) {
            z = 1;
        }
        return z;
    }

    public Integer Beta(Integer x, Integer y) {
        int z = 0;
        if (x == 0 && y == 0) {
            z = 0;
        } else if (x == 0 && y == 1) {
            z = 0;
        } else if (x == 1 && y == 0) {
            z = 0;
        } else if (x == 1 && y == 1) {
            z = 1;
        } else if (x == 2 && y == 0) {
            z = 1;
        } else if (x == 2 && y == 1) {
            z = 1;
        }
        return z;
    }

    public Double[] Restriction(Double[] support, Double[] vector) {
        List<Double> restricted = new ArrayList(Arrays.asList(vector));

        for (int i = support.length - 1; i >= 0; i--) {
            if (support[i] == 1.0) {
                restricted.remove(i);
            }
        }

        return restricted.toArray(new Double[restricted.size()]);
    }

    public Double[] Negation(Double[] vector) {
        Double[] negation = new Double[vector.length];

        for (int i = 0; i < negation.length; i++) {
            negation[i] = (vector[i] == 1.0) ? 0.0 : 1.0;
        }

        return negation;
    }

    public Integer UB(Double[] vector) {
        Integer ub = 0;

        for (Double value: vector) {
            ub += Beta(value.intValue(), value.intValue());
        }

        return ub;
    }

    public Double[] TransformedTao(Double[] x, Double[] y) {
        Double[] transformed = new Double[x.length];

        for (int i = 0; i < transformed.length; i++) {
            transformed[i] = (double) Beta(x[i].intValue(), Alfa(0, y[i].intValue()));
        }

        return transformed;
    }

    public Integer TransformedTheta(Double[] x, Double[] y) {
        return UB(TransformedTao(x, y)) + UB(TransformedTao(y, x));
    }

}
