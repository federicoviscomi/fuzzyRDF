package tnorms;

import java.math.BigDecimal;

public interface TnormFunction {
	/**
	 * Calcola una norma trinagolare dei due numeri <param>a</param> e
	 * <param>b</param>.
	 * 
	 * @param a
	 * @param b
	 * @return la norma trinagolare dei due numeri <param>a</param> e
	 *         <param>b</param>.
	 */
	public double tnorm(BigDecimal a, BigDecimal b);

}
