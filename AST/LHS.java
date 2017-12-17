package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public class LHS extends ASTNode{

	public String getName() {
		return name;
	}

	public final String name;
	public final Index index;
	private Type type;
	private boolean isCartesian;
	private Declaration dec;

	public boolean isCartesian() {
		return isCartesian;
	}

	public Index getIndex() {
		return index;
	}

	public Declaration getDec() {
		return dec;
	}

	public void setDec(Declaration dec) {
		this.dec = dec;
	}

	public void setCartesian(boolean isCartesian) {
		this.isCartesian = isCartesian;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public LHS(Token firstToken, Token name, Index index) {
		super(firstToken);
		this.name = name.getText();
		this.index = index;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitLHS(this,arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LHS other = (LHS) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("name [name=");
		builder.append(name);
		builder.append(", index=");
		builder.append(index);
		builder.append("]");
		return builder.toString();
	}


	
	
}
