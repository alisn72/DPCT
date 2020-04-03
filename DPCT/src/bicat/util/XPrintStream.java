package bicat.util;

import java.io.*;

/**
 * PrintStream to print text to console and file simultaneously.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public class XPrintStream
{
	private final OutputStream outStream;
	private final PrintStream out;

	public XPrintStream(String filename) throws IOException
	{
		outStream = new FileOutputStream(new File(filename));
		out = new PrintStream(outStream);
	}

	public XPrintStream(OutputStream outStream)
	{
		this.outStream = null;
		out = new PrintStream(outStream);
	}

	public XPrintStream(PrintStream out)
	{
		outStream = null;
		this.out = out;
	}

	public void print(char c)
	{
		out.print(c);
		System.out.print(c);
	}

	public void print(char[] parm1)
	{
		out.print(parm1);
		System.out.print(parm1);
	}

	public void print(String s)
	{
		out.print(s);
		System.out.print(s);
	}

	public void print(boolean b)
	{
		out.print(b);
		System.out.print(b);
	}

	public void print(int i)
	{
		out.print(i);
		System.out.print(i);
	}

	public void print(long l)
	{
		out.print(l);
		System.out.print(l);
	}

	public void print(float f)
	{
		out.print(f);
		System.out.print(f);
	}

	public void print(double d)
	{
		out.print(d);
		System.out.print(d);
	}

	public void print(Object obj)
	{
		out.print(obj);
		System.out.print(obj);
	}

	public void println()
	{
		out.println();
		System.out.println();
	}

	public void println(String s)
	{
		out.println(s);
		System.out.println(s);
	}

	public void println(char c)
	{
		out.println(c);
		System.out.println(c);
	}

	public void println(char[] parm1)
	{
		out.println(parm1);
		System.out.println(parm1);
	}

	public void println(boolean b)
	{
		out.println(b);
		System.out.println(b);
	}

	public void println(int i)
	{
		out.println(i);
		System.out.println(i);
	}

	public void println(long l)
	{
		out.println(l);
		System.out.println(l);
	}

	public void println(float f)
	{
		out.println(f);
		System.out.println(f);
	}

	public void println(double d)
	{
		out.println(d);
		System.out.println(d);
	}

	public void println(Object obj)
	{
		out.println(obj);
		System.out.println(obj);
	}

	public void flush()
	{
		out.flush();
		System.out.flush();
	}

	public void close()
	{
		if (outStream!=null)
			try
			{
				outStream.close();
			}
			catch (Exception e)
			{
			}

		out.close();
	}
}
