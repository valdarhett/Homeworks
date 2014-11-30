package calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import calculator.Lexer;
import calculator.Type;
import calculator.token;

public class Parser2 {
	
	public enum numType
	{
		SCALAR,
		POINT,
		INCH;
	}
	
	private Lexer lexer;
	
	public Parser2(Lexer lexer)
	{
		this.lexer = lexer;
	}

	public String evaluate() throws ParseException {
		
		ArrayList<token> buf = this.lexer.returnTokens;
		//solveMain(buf);
		number n = compute2(buf);
		//System.out.println("final answer : :: : " + n.toString());
		StringBuilder sb = new StringBuilder();
//		
//		for(token t : buf)
//		{
//			sb.append(t.getValue());
//		}
		sb.append(n.toString());
		return sb.toString();
		
	}
	
	public double SOLVE1(List<token> list)
	{
		double l_operand = 0.0;
		double r_operand = 0.0;
		double result = 0.0;
		if(list.get(0) == null)
			return 0.0;
		if(list.get(0).getType()==Type.L_PAREN)
		{
			l_operand = SOLVE1(list.subList(1, -1));
		}
		if(list.get(0).getType() == Type.NUMBER)
		{
			l_operand = Double.valueOf(list.get(0).getValue());
			if(list.get(1).getType() == Type.PLUS)
			{
				r_operand =Double.valueOf(list.get(2).getValue());
				result = l_operand + r_operand;
			}
			if(list.get(1).getType() == Type.INCH)
			{
				if(list.get(2).getType() == Type.PLUS)
				{
					if(list.get(4).getType() == Type.INCH)
					{
						
					}
					else if(list.get(4).getType() == Type.POINT)
					{
						
					}
					else {
						r_operand =Double.valueOf(list.get(3).getValue());
						result = l_operand + r_operand;
					}
					
				}
			}
			else if(list.get(1).getType() == Type.POINT){
				
			}
		}
		
		
		////System.out.println(result);
		return result;
	
	}
	
	public void solveMain(ArrayList<token> list)
	{
		ArrayList<Integer> left = findLeftParen(list);
		ArrayList<Integer> right = findRightParen(list);
		
		for(int i = 0; i < left.size(); i++)
		{
			//System.out.println(left.get(i)+" "+ right.get(i));
		}
	}
	
	public ArrayList<Integer> findLeftParen(List<token> _List)
	{
		ArrayList<Integer> reLeft = new ArrayList<Integer>();
		for(int i = 0; i < _List.size();i++)
		{
			if(_List.get(i).getType()==Type.L_PAREN)
			{
				reLeft.add(i);
			}
		}
		return reLeft;
	}
	
	public ArrayList<Integer> findRightParen(List<token> _List)
	{
		//System.out.println("in findRightParen");
		ArrayList<Integer> reRight = new ArrayList<Integer>();
		for(int i = 0; i < _List.size();i++)
		{
			if(_List.get(i).getType()==Type.R_PAREN)
			{
				reRight.add(i);
			}
		}
		return reRight;
	}
	
	
	public number compute2(List<token> sublist) throws ParseException
	{
		ArrayList<number> operands = new ArrayList<number>();
		token operator = null;
		number result = null;
		
		
		for(int i = 0; i < sublist.size(); i++)
		{	
			if(sublist.get(i).getType() == Type.L_PAREN)
			{ 
				int index = i;
				int rightParenIndex = 0;
				for(int j = index; j < sublist.size(); j++)
				{
					if(sublist.get(j).getType() == Type.R_PAREN)
					{
						rightParenIndex = j;
						break;
					}
				}
				if(rightParenIndex + 1 <= sublist.size()-1)
				{
					if(sublist.get(rightParenIndex+1).getType() == Type.INCH)
					{
						number temp = compute2(sublist.subList(i+1, rightParenIndex));
						number n = null;
						if(temp.getType() == numType.POINT)
						{
							n = number.unitConversion_PtToIn(temp);
						}
						else {
							n = new number(temp.getValue(), numType.INCH);
						}
						operands.add(n);
					}
					else if(sublist.get(rightParenIndex+1).getType() == Type.POINT)
					{
						number temp = compute2(sublist.subList(i+1, rightParenIndex));
						number n = null;
						if(temp.getType() == numType.INCH)
						{
							n = number.unitConversion_InToPt(temp);
						}
						else {
							n = new number(temp.getValue(), numType.POINT);
						}
						//number n = new number(compute2(sublist.subList(i+1, rightParenIndex)).getValue(), numType.POINT);
						operands.add(n);
					}	
					else {
						operands.add(compute2(sublist.subList(i+1, rightParenIndex+1)));						
					}
				}
				else {
					operands.add(compute2(sublist.subList(i+1, rightParenIndex)));
				}
				//operands.add(compute2(sublist.subList(i+1, rightParenIndex)));
				i = rightParenIndex;
			}
			
			if(sublist.get(i).getType() == Type.NUMBER)
			{
				if(i== (sublist.size() - 1))
				{
					number n = new number(Double.valueOf(sublist.get(i).getValue()), numType.SCALAR);
					//System.out.println(n.toString());
					operands.add(n);
				}
				else 
				{
					
					if(sublist.get(i+1).getType() == Type.INCH) 
					{
						number n = new number(Double.valueOf(sublist.get(i).getValue()), numType.INCH);
					
						//System.out.println(n.toString());
						operands.add(n);
					}
					else if(sublist.get(i+1).getType() == Type.POINT) 
					{
						number n = new number(Double.valueOf(sublist.get(i).getValue()), numType.POINT);
						//System.out.println(n.toString());
						operands.add(n);
					}
					else {
						number n = new number(Double.valueOf(sublist.get(i).getValue()), numType.SCALAR);
						//System.out.println(n.toString());
						operands.add(n);
					}
				}
					
			}
			if(sublist.get(i).getType() == Type.PLUS || sublist.get(i).getType()==Type.MINUS || sublist.get(i).getType()==Type.DIVIDE || sublist.get(i).getType()==Type.TIMES)
			{
				operator = sublist.get(i);
			}
//			
//			if(sublist.get(i).getType() == Type.R_PAREN)
//			{
//				
//			}			
		}
		
		if(operands.size()==1)
		{
			return operands.get(0);
		}
		
		if(operands.size()>=3)
		{
			System.out.println("Error: invalid expression");
		}
		
		else {
			if(operator.getType() == Type.PLUS)
			{
				result =  number.add(operands.get(0), operands.get(1));
				return result;
			}
			if(operator.getType() == Type.MINUS)
			{
				
				result =  number.subtract(operands.get(0), operands.get(1));
				return result;
			}
			if(operator.getType() == Type.DIVIDE)
			{
				result = number.divide(operands.get(0), operands.get(1));
				return result;
			}
			if(operator.getType() == Type.TIMES)
			{
				result =  number.multiply(operands.get(0), operands.get(1));
				return result;
			}
		}

		return result;
	}
	
	
	public number calculate(ArrayList<number> operands, token operator)
	{
		number result = null;
		if(operands.size()==1)
		{
			return operands.get(0);
		}
		
		if(operands.size()>=3)
		{
			System.out.println("Error: invalid expression");
		}
		
		else {
			if(operator.getType() == Type.PLUS)
			{
				result =  number.add(operands.get(0), operands.get(1));
				return result;
			}
			if(operator.getType() == Type.MINUS)
			{
				
				result =  number.subtract(operands.get(0), operands.get(1));
				return result;
			}
			if(operator.getType() == Type.DIVIDE)
			{
				result = number.divide(operands.get(0), operands.get(1));
				return result;
			}
			if(operator.getType() == Type.TIMES)
			{
				result =  number.multiply(operands.get(0), operands.get(1));
				return result;
			}
		}
		return result;
	}
	
	
	
	
	public void FINALCOMPUTE(ArrayList<number> operands, ArrayList<token> operators, List<token> subList)
	{
		
	}
	
	
	public int findRParen(List<token> sublist)
	{
		for(int i = 0 ; i < sublist.size(); i++)
		{
			if(sublist.get(i).getType() == Type.R_PAREN)
				return i;
		}
		
		return 0;
	}


	public static class number
	{
		private Double value;
		private numType type;
		public number(Double value, numType type) {
			this.value = value;
			this.type = type;
		}
		public numType getType() {
			return type;
		}
		public Double getValue() {
			return value;
		}
		
		public static number add(number operand1, number operand2)
		{
			number result = null;
			if(operand2.getType() != operand1.getType())
			{
				if(operand1.getType() == numType.INCH)
				{
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue() + unitConversion_PtToIn(operand2).getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() + operand2.getValue()), numType.INCH);
					}
				}
				if(operand1.getType() == numType.POINT)
				{
					if(operand2.getType()== numType.INCH)
					{
						result = new number((operand1.getValue()+unitConversion_InToPt(operand2).getValue()), numType.POINT);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() + operand2.getValue()), numType.POINT);
					}
				}
				if(operand1.getType() == numType.SCALAR)
				{
					if(operand2.getType() == numType.INCH)
					{
						result = new number((operand1.getValue()+operand2.getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue()+operand2.getValue()),numType.POINT);
					}
				}
			}
			else {
				result = new number((operand1.getValue()+operand2.getValue()), operand1.getType());
			}
			return result;
		}
		
		public static number subtract(number operand1, number operand2)
		{
			number result = null;
			if(operand2.getType() != operand1.getType())
			{
				if(operand1.getType() == numType.INCH)
				{
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue() - unitConversion_PtToIn(operand2).getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() - operand2.getValue()), numType.INCH);
					}
				}
				if(operand1.getType() == numType.POINT)
				{
					if(operand2.getType()== numType.INCH)
					{
						result = new number((operand1.getValue()-unitConversion_InToPt(operand2).getValue()), numType.POINT);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() - operand2.getValue()), numType.POINT);
					}
				}
				if(operand1.getType() == numType.SCALAR)
				{
					if(operand2.getType() == numType.INCH)
					{
						result = new number((operand1.getValue()-operand2.getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue()-operand2.getValue()),numType.POINT);
					}
				}
			}
			else {
				result = new number((operand1.getValue()-operand2.getValue()), operand1.getType());
			}
			return result;
		}
		
		
		public static number divide(number operand1, number operand2)
		{
			number result = null;
			if(operand2.getType() != operand1.getType())
			{
				if(operand1.getType() == numType.INCH)
				{
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue() / unitConversion_PtToIn(operand2).getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() / operand2.getValue()), numType.INCH);
					}
				}
				if(operand1.getType() == numType.POINT)
				{
					if(operand2.getType()== numType.INCH)
					{
						result = new number((operand1.getValue()/unitConversion_InToPt(operand2).getValue()), numType.POINT);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() / operand2.getValue()), numType.INCH);
					}
				}
				if(operand1.getType() == numType.SCALAR)
				{
					if(operand2.getType() == numType.INCH)
					{
						result = new number((operand1.getValue()/operand2.getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue()/operand2.getValue()),numType.POINT);
					}
				}
			}
			else {
				result = new number((operand1.getValue()/operand2.getValue()), numType.SCALAR);
			}
			return result;
		}
		
		public static number multiply(number operand1, number operand2)
		{
			number result = null;
			if(operand2.getType() != operand1.getType())
			{
				if(operand1.getType() == numType.INCH)
				{
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue() * unitConversion_PtToIn(operand2).getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() * operand2.getValue()), numType.INCH);
					}
				}
				if(operand1.getType() == numType.POINT)
				{
					if(operand2.getType()== numType.INCH)
					{
						result = new number((operand1.getValue()*unitConversion_InToPt(operand2).getValue()), numType.POINT);
					}
					if(operand2.getType() == numType.SCALAR)
					{
						result = new number((operand1.getValue() * operand2.getValue()), numType.INCH);
					}
				}
				if(operand1.getType() == numType.SCALAR)
				{
					if(operand2.getType() == numType.INCH)
					{
						result = new number((operand1.getValue()*operand2.getValue()),numType.INCH);
					}
					if(operand2.getType() == numType.POINT)
					{
						result = new number((operand1.getValue()*operand2.getValue()),numType.POINT);
					}
				}
			}
			else {
				result = new number((operand1.getValue()*operand2.getValue()), operand1.getType());
			}
			return result;
		}
		
		
		
		@Override
		public String toString() {
			
			this.value = round(this.value, 2);
			String valueString = this.value.toString();
			
			if(this.type == numType.INCH)
				return valueString+" in";

			if(this.type == numType.POINT)
				return valueString+" pt";

			if(this.type == numType.SCALAR)
				return valueString;
			
			return super.toString();
		}
		
		public static double round(double value, int places) {
		    if (places < 0) throw new IllegalArgumentException();

		    BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
		}
		
		public static number unitConversion_InToPt(number in)
		{
			return new number(in.getValue() * 72.0, numType.POINT);
		}

		public static number unitConversion_PtToIn(number pt)
		{
			return new number(pt.getValue()/72.0, numType.INCH);
		}
		
	}
	
}


