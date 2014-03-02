package tnorms;

import java.math.BigDecimal;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;

import fuzzyRDFSEngine.FuzzySystem;

public class GenericTnormFunctor implements Builtin {

	private static final TnormFunction goedelTnorm = new TnormFunction() {
		@Override
		public double tnorm(BigDecimal degree1, BigDecimal degree2) {
			return degree1.min(degree2).doubleValue();
		}
	};
	private static final TnormFunction productTnorm = new TnormFunction() {
		@Override
		public double tnorm(BigDecimal a, BigDecimal b) {
			return a.multiply(b).doubleValue();
		}
	};
	private static final TnormFunction lucasiewiczTnorm = new TnormFunction() {
		@Override
		public double tnorm(BigDecimal a, BigDecimal b) {
			return Math.max(0, a.add(b).subtract(BigDecimal.ONE).doubleValue());
		}
	};
	private static final TnormFunction drasticTnorm = new TnormFunction() {
		@Override
		public double tnorm(BigDecimal a, BigDecimal b) {
			if (a.equals(BigDecimal.ONE))
				return b.doubleValue();
			if (b.equals(BigDecimal.ONE))
				return a.doubleValue();
			return 0;
		}
	};
	private static final TnormFunction nilpotentMinimumTnorm = new TnormFunction() {
		@Override
		public double tnorm(BigDecimal a, BigDecimal b) {
			if (a.add(b).doubleValue() > 1)
				return Math.min(a.doubleValue(), b.doubleValue());
			return 0;
		}
	};
	private static final TnormFunction hamacherProductTnorm = new TnormFunction() {
		@Override
		public double tnorm(BigDecimal a, BigDecimal b) {
			if (a.equals(BigDecimal.ZERO) && b.equals(BigDecimal.ZERO))
				return 0;
			return (a.multiply(b).divide(a.add(b).subtract(a.multiply(b))))
					.doubleValue();
		}
	};

	public static final TnormFunction predefinedTnormArray[] = {
			GenericTnormFunctor.goedelTnorm, GenericTnormFunctor.productTnorm,
			GenericTnormFunctor.lucasiewiczTnorm,
			GenericTnormFunctor.drasticTnorm,
			GenericTnormFunctor.nilpotentMinimumTnorm,
			GenericTnormFunctor.hamacherProductTnorm };

	public static final Builtin builtin = new GenericTnormFunctor();
	private TnormFunction and = GenericTnormFunctor.goedelTnorm;

	@Override
	public boolean bodyCall(Node[] argv, int argc, RuleContext context) {
		String degree1 = getArg(0, argv, context).getLiteralValue().toString();
		String degree2 = getArg(1, argv, context).getLiteralValue().toString();
		double tnorm = and.tnorm(new BigDecimal(degree1), new BigDecimal(
				degree2));
		return context.getEnv().bind(getArg(2, argv, context),
				Node.createLiteral(tnorm + ""));
	}

	/**
	 * Return the n'th argument node after dereferencing by what ever type of rule
	 * engine binding environment is appropriate.
	 */
	public Node getArg(int n, Node[] args, RuleContext context) {
		return context.getEnv().getGroundVersion(args[n]);
	}

	@Override
	public int getArgLength() {
		return 3;
	}

	@Override
	public String getName() {
		return "tnorm";
	}

	@Override
	public String getURI() {
		return FuzzySystem.degree.getNameSpace() + this.getName();
	}

	@Override
	public void headAction(Node[] argv, int argc, RuleContext context) {
		this.bodyCall(argv, argc, context);
	}

	@Override
	public boolean isMonotonic() {
		return true;
	}

	@Override
	public boolean isSafe() {
		return true;
	}

	public void setTnorm(TnormFunction tnorm) {
		this.and = tnorm;
	}
}
