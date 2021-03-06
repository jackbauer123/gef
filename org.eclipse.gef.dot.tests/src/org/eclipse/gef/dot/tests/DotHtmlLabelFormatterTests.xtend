/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #532216)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.xtext.formatting.INodeModelFormatter
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.resource.XtextResource
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelInjectorProvider)
class DotHtmlLabelFormatterTests {
	
	@Inject extension ParseHelper<HtmlLabel>
	@Inject extension INodeModelFormatter

	@Test def testFormatting001(){
		'''<BR/>'''
		.assertFormattedAs('''
			<BR/>
		''')
	}

	@Test def testFormatting002(){
		'''<BR />'''
		.assertFormattedAs('''
			<BR/>
		''')
	}

	@Test def testFormatting003(){
		'''<BR  />'''
		.assertFormattedAs('''
			<BR/>
		''')
	}

	@Test def testFormatting004(){
		'''<BR ALIGN="LEFT"/>'''
		.assertFormattedAs('''
			<BR ALIGN="LEFT" />
		''')
	}
	
	@Test def testFormatting005(){
		'''<S>strike-through</S>'''
		.assertFormattedAs('''
			<S>
				strike-through
			</S>
		''')
	}
	
	@Test def testFormatting006(){
		'''
			<S>strike-through</S><B>bold text</B>'''
		.assertFormattedAs('''
			<S>
				strike-through
			</S>
			<B>
				bold text
			</B>
		''')
	}
	
	@Test def testFormatting007(){
		'''
			<S>strike-through</S><BR/>'''
		.assertFormattedAs('''
			<S>
				strike-through
			</S>
			<BR/>
		''')
	}
	
	@Test def testFormatting008(){
		'''
			<BR/><BR/>'''
		.assertFormattedAs('''
			<BR/>
			<BR/>
		''')
	}
	
	@Test def testFormatting009(){
		'''
			<BR/><S>strike-through</S>'''
		.assertFormattedAs('''
			<BR/>
			<S>
				strike-through
			</S>
		''')
	}
	
	@Test def testFormatting010(){
		'''<TABLE><TR><TD>text</TD></TR></TABLE>'''
		.assertFormattedAs('''
			<TABLE>
				<TR>
					<TD>
						text
					</TD>
				</TR>
			</TABLE>
		''')
	}

	@Test def testFormatting011(){
		'''<FONT color="green"><TABLE><TR><TD>text</TD></TR></TABLE></FONT>'''
		.assertFormattedAs('''
			<FONT color="green">
				<TABLE>
					<TR>
						<TD>
							text
						</TD>
					</TR>
				</TABLE>
			</FONT>
		''')
	}

	@Test def testFormatting012(){
		'''<FONT POINT-SIZE="24.0">line</FONT>'''
		.assertFormattedAs('''
			<FONT POINT-SIZE="24.0">
				line
			</FONT>
		''')
	}
	
	@Test def testFormatting013(){
		'''<FONT POINT-SIZE="24.0"  COLOR="blue" >line</FONT>'''
		.assertFormattedAs('''
			<FONT POINT-SIZE="24.0" COLOR="blue">
				line
			</FONT>
		''')
	}

	@Test def testFormatting014(){
		'''<table><tr><td>first</td></tr><tr><td><table><tr><td><b>second</b></td></tr></table></td></tr></table>'''
		.assertFormattedAs('''
			<table>
				<tr>
					<td>
						first
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<b>
										second
									</b>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		''')
	}
	
	@Test def testFormatting015(){
		'''<TABLE><TR><TD>nord<BR/>west</TD><VR/><TD>nord<BR/>east</TD></TR><HR/><TR><TD>south<BR/>west</TD><VR/><TD>south<BR/>east</TD></TR></TABLE>'''
		.assertFormattedAs('''
			<TABLE>
				<TR>
					<TD>
						nord
						<BR/>
						west
					</TD>
					<VR/>
					<TD>
						nord
						<BR/>
						east
					</TD>
				</TR>
				<HR/>
				<TR>
					<TD>
						south
						<BR/>
						west
					</TD>
					<VR/>
					<TD>
						south
						<BR/>
						east
					</TD>
				</TR>
			</TABLE>
		''')
	}
	
	@Test def testFormatting016(){
		'''<font>font</font><i>i</i><b>b</b><u>u</u><o>o</o><sub>sub</sub><sup>sup</sup><s>s</s>'''
		.assertFormattedAs('''
			<font>
				font
			</font>
			<i>
				i
			</i>
			<b>
				b
			</b>
			<u>
				u
			</u>
			<o>
				o
			</o>
			<sub>
				sub
			</sub>
			<sup>
				sup
			</sup>
			<s>
				s
			</s>
		''')
	}	
	
	private def assertFormattedAs(CharSequence input, CharSequence expected){
		expected.toString.trim.assertEquals(input.formattedText)
	}
	
	private def formattedText(CharSequence unformattedText){
		val rootNode = (unformattedText.parse.eResource as XtextResource).parseResult.rootNode
		rootNode.format(0, unformattedText.length).formattedText
	}
}