/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.client.pmi;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.pmi.stat.AverageStatisticImpl")
public class AverageStatistic extends Statistic {
    private static final long serialVersionUID = 532089977446362907L;

    private long count;
    private long min;
    private long max;
    private long total;
    private double sumOfSquares;
    private AverageStatistic baseValue;

    public long getCount() {
        return count;
    }
    
    public long getTotal() {
        return total;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public double getSumOfSquares() {
        return sumOfSquares;
    }

    public double getMean() {
        return count == 0 ? 0 : (double)total / (double)count;
    }

    @Override
    void format(StringBuilder buffer) {
        super.format(buffer);
        buffer.append(", avg=");
        buffer.append(getMean());
        buffer.append(", min=");
        buffer.append(min);
        buffer.append(", max=");
        buffer.append(max);
        buffer.append(", total=");
        buffer.append(total);
        buffer.append(", count=");
        buffer.append(count);
        buffer.append(", sumSq=");
        buffer.append(sumOfSquares);
    }
}
