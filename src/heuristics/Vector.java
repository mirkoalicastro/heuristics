package heuristics;

import java.util.Arrays;

/**
 * A vector representation of a fixed length of all double values and with an
 * additional double value.
 * @author Mirko Alicastro {@link https://mirkoalicastro.com}
 */
public class Vector {
    protected double value;
    protected double[] array;
    
    /**
     * Creates a vector that is a copy of another vector.
     * The two vectors are independent.
     * @param other the vector to copy
     */
    public Vector(Vector other) {
        this(other.array.length);
        this.value = other.value;
        System.arraycopy(other.array, 0, array, 0, array.length);
    }

    /**
     * Creates a vector that contains the given double array and with a value of
     * 0.
     * No allocation is made so the double array should not modified by external
     * methods.
     * @param array the double array this vector must use
     */
    public Vector(double[] array) {
        this.array = array;
    }
    
    /**
     * Creates a vector with an array of a specified length with all zero
     * entries and with a value of 0.
     * @param length the array length
     */
    public Vector(int length) {
        this.array = new double[length];
    }
    
    /**
     * Sets the <i>index</i>-th cell of the array equal to <i>value</i>.
     * @param index the index of the array cell
     * @param value the value to set
     * @return the <i>this</i> instance
     */
    public Vector set(int index, double value) {
        array[index] = value;
        return this;
    }

    /**
     * Returns the value of the <i>index</i>-th cell.
     * @param index the index of the array cell
     * @return the value of that cell
     */
    public double get(int index) {
        return array[index];
    }
    
    /**
     * Returns the array length.
     * @return the array length
     */
    public int getLength() {
        return array.length;
    }
    
    /**
     * Returns the vector value.
     * @return the vector value
     */
    public double getValue() {
        return value;
    }
    
    /**
     * Sets the vector value.
     * @param value the vector value to set
     * @return the <i>this</i> instance
     */
    public Vector setValue(double value) {
        this.value = value;
        return this;
    }
    
    /**
     * Returns <tt>true</tt> if the two specified vectors contains arrays of
     * doubles that are <i>equal</i> among them.
     * The vector value is not taken into account for equality test.
     * @param other the other vector to be tested for equality
     * @return <tt>true</tt> if the two vectors are equal
     * @see Arrays#equals(double[], double[]) 
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Vector))
            return false;
        Vector vector = (Vector) other;
        return Arrays.equals(vector.array, array);
    }

    /**
     * Returns a hash code based on the contents of the specified array.
     * @return a content-based hash code.
     * @see Arrays#hashCode(double[]) 
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
    
    /**
     * Returns a string representation of the contents of the vector.
     * The string representation consists of:
     * <ul>
     * <li>A left brace <tt>{</tt></li>
     * <li>A string representation of the vector value, i.e., <tt>value:</tt>
     * followed by the vector value</li>
     * <li>A commma <tt>,</tt> followed by a space</li>
     * <li>The string representation of the vector array, i.e., <tt>array:</tt>
     * followed by the array string representation given by
     * <i>Arrays.toString(double[])</i> method</li>
     * <li>A right brace <tt>}</tt></li>
     * </ul>
     * @return a string representation of this vector
     * @see Arrays#toString(double[]) 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{value:").append(value).append(", array:")
                .append(Arrays.toString(array)).append("}");
        return sb.toString();
    }
}
