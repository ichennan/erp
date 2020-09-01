<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:fo="http://www.w3.org/1999/XSL/Format" 
				exclude-result-prefixes="fo">

  	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  	<xsl:param name="versionParam" select="'1.0'"/> 
  	<!-- ========================= -->
  	<!-- root element: projectteam -->
  	<!-- ========================= -->
  	<xsl:template match="article">
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="'Arial', 'SimSun'" font-size="10pt">
	      	<fo:layout-master-set>
        		<fo:simple-page-master master-name="simpleA4" 
        							   page-height="29.7cm" 
        							   page-width="21cm" 
        							   margin-top="5mm" 
        							   margin-bottom="0mm" 
        							   margin-left="1cm" 
        							   margin-right="1cm">
          			<fo:region-body region-name="content" margin-top="25mm" margin-bottom="20mm"/>
          			<fo:region-before region-name="header" extent="25mm"/>
					<fo:region-after region-name="footer" extent="20mm"/>          			
        		</fo:simple-page-master>
        		<fo:simple-page-master	master-name="last-page" 
        							   page-height="29.7cm" 
        							   page-width="21cm" 
        							   margin-top="5mm" 
        							   margin-bottom="0mm" 
        							   margin-left="0mm" 
        							   margin-right="0mm">
          			<fo:region-body region-name="content" margin-top="47mm" margin-bottom="0mm"/>
          			<fo:region-before region-name="evaluationLabel" extent="47mm"/>
				</fo:simple-page-master>
				
				<fo:page-sequence-master master-name="different-master">
				    <fo:repeatable-page-master-alternatives>
			        <fo:conditional-page-master-reference master-reference="last-page" page-position="last"/>
		            <fo:conditional-page-master-reference master-reference="simpleA4" />
				    </fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
        		
      		</fo:layout-master-set>

	      	<fo:page-sequence master-reference="different-master">      	
				
				<fo:static-content flow-name="header">
					<fo:table table-layout="fixed" width="100%">
				  		<fo:table-column column-width="75mm"/>
				  		<fo:table-column column-width="115mm" />
				  		<fo:table-body>
				    		<fo:table-row>
				      			<fo:table-cell>
				        			<fo:block>
				        				<fo:external-graphic src="{logoFolder}/CompLogos/Cortina Group.jpg" content-width="scale-to-fit"  width="7cm"/>
				        			</fo:block>
				      			</fo:table-cell>
				      			<fo:table-cell text-align="right" display-align="after">
				      				<fo:block  margin-bottom="5pt">COR F32/01 SAMPLE ORDER rev_44</fo:block>
				      			</fo:table-cell>
				    		</fo:table-row>
				    		<fo:table-row border-top="2pt solid black" background-color="{collectionTypeColor}">
				      			<fo:table-cell display-align="center">
				        			<fo:block font-size="22pt" margin-top="5pt">
										<fo:inline font-weight="bold"><xsl:value-of select="articleNumber"/></fo:inline>
										<fo:inline padding-left="5mm">/<xsl:value-of select="season"/></fo:inline>
										<fo:inline><xsl:value-of select="srSequence"/></fo:inline>
				        			</fo:block>
				      			</fo:table-cell>
				      			<fo:table-cell text-align="right" font-weight="bold" display-align="after">
				      				<fo:block font-size="12pt" margin-bottom="3pt"><xsl:value-of select="collectionTypeDescription"/></fo:block>
				      			</fo:table-cell>
				    		</fo:table-row>
				  		</fo:table-body>
					</fo:table>
				</fo:static-content> 
				
				<fo:static-content flow-name="evaluationLabel" font-size="8pt" color="black">
					<fo:table table-layout="fixed" width="19cm">
						<fo:table-column column-width="1cm"/>
						<fo:table-column column-width="11cm"/>
						<fo:table-column column-width="8cm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:table table-layout="fixed" width="100%">
										<fo:table-column column-width="9.5cm"/>
										<fo:table-column column-width="1.5cm"/>
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell padding-left="0.3cm">
													<fo:block><xsl:value-of select="collectionManager"/>/<xsl:value-of select="productManager"/>/<xsl:value-of select="designer"/>/<xsl:value-of select="juniorDesigner"/></fo:block>
												</fo:table-cell>
												<fo:table-cell text-align="right" padding-right="0.3cm">
													<fo:block><xsl:value-of select="season"/></fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									<fo:table table-layout="fixed" width="100%" display-align="center" margin="0.2cm, 0">
										<fo:table-column column-width="4cm"/>
										<fo:table-column column-width="5.5cm"/>
										<fo:table-column column-width="1.5cm"/>
										<fo:table-body border="1px solid #888888">
											<fo:table-row height="0.7cm">
												<fo:table-cell padding-left="0.3cm">
													<fo:block text-align="left">
														<fo:external-graphic src="{barcodeFolder}/{articleNumber}.PNG" content-width="scale-to-fit"  width="2cm"/>
													</fo:block>	
												</fo:table-cell>
												<fo:table-cell number-rows-spanned="2">
													<fo:block font-size="12pt"><xsl:value-of select="collectionTypeDescriptionSimple"/></fo:block>
												</fo:table-cell>
												<fo:table-cell text-align="right" padding-right="0.3cm">
													<fo:block></fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row>
												<fo:table-cell font-size="12pt" font-weight="bold" padding-left="0.5cm">
													<fo:block><xsl:value-of select="articleNumber"/></fo:block>
												</fo:table-cell>
												<fo:table-cell font-size="12pt" font-weight="bold" text-align="right" padding-right="0.3cm">
													<fo:block></fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									
									<fo:table table-layout="fixed" width="100%">
										<fo:table-column column-width="8cm"/>
										<fo:table-column column-width="3cm"/>
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell padding-left="0.3cm">
													<fo:block><fo:inline font-size="9pt" font-weight="bold">UPPER:</fo:inline><xsl:value-of select="upper"/></fo:block>
													<fo:block><fo:inline font-size="9pt" font-weight="bold">LINING:</fo:inline><xsl:value-of select="lining"/></fo:block>
													<fo:block><fo:inline font-size="9pt" font-weight="bold">SOCK:</fo:inline><xsl:value-of select="sockLining"/></fo:block>
													<fo:block><fo:inline font-size="9pt" font-weight="bold">SMARTIFICATIONS:</fo:inline></fo:block>
													<fo:block><xsl:value-of select="simpleRemark"/></fo:block>
												</fo:table-cell>
												<fo:table-cell text-align="center" >
								<xsl:if test="basedOn != ''">
													<fo:block>Reference article</fo:block>
													<fo:block>
														<fo:external-graphic src="{imageFolder}/{basedOnBrand}/{basedOn}.JPG" content-width="scale-to-fit"  width="2cm"/>
													</fo:block>
													<fo:block font-weight="bold"><xsl:value-of select="basedOn"/></fo:block>
													<fo:block font-weight="bold">RRP: <xsl:value-of select="basedOnRrp"/></fo:block>
								</xsl:if>
													<fo:block></fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:table-cell>
								
								<fo:table-cell padding-left="1cm">
									<fo:block font-weight="bold"><fo:inline font-size="6pt" font-weight="normal">EVALUATION</fo:inline><fo:inline padding-left="1cm">OK</fo:inline><fo:inline padding-left="0.9cm">NOT OK</fo:inline><fo:inline padding-left="0.9cm">CANCEL</fo:inline></fo:block>
									<fo:table table-layout="fixed" width="100%" height="3cm" border="1px solid black" margin="0.2cm, 0">
										<fo:table-column column-width="7cm"/>
										<fo:table-body>
											<fo:table-row height="3.5cm">
												<fo:table-cell>
													<fo:block>
													</fo:block>	
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									<fo:block font-weight="bold">
										<fo:table table-layout="fixed" width="100%">
											<fo:table-column column-width="2.2cm"/>
											<fo:table-column column-width="1.3cm"/>
											<fo:table-column column-width="3.5cm"/>
											<fo:table-body>
												<fo:table-row>
													<fo:table-cell>
														<fo:block><xsl:value-of select="last"/></fo:block>
													</fo:table-cell>
													<fo:table-cell text-align="center">
														<fo:block><xsl:value-of select="mould"/></fo:block>
													</fo:table-cell>
													<fo:table-cell text-align="right">
														<fo:block><xsl:value-of select="heel"/></fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content> 	
				
				<fo:static-content flow-name="footer" font-size="7pt" color="#666666">
					<fo:block border-top="2pt solid black" padding-top="3pt"  margin-bottom="1pt">ATTENTION!!!! These samples are made under copyright of CORTINA NV. CORTINA styles cannot be sold to any other customer. If CORTINA styles are found on the market outside CORTINA MARKET SYSTEMS, a fine of 5000 USD will be charged to the party which received the original sample order.</fo:block>
					<fo:block >警告：CORTINA专业设计鞋款版权归CORTINA公司专有，任何供货商不得将此款式推荐和销售给其他客人！如果本公司在市场上发现他人未经我司授权而非法销售我CORTINA公司鞋款的侵权行为，我司将立即对承接我司开发样款的而未遵循保密原则的供货商处以5000美元的罚款。</fo:block>
				</fo:static-content> 	

	      	
	        	<fo:flow flow-name="content" >
					<fo:table table-layout="fixed" width="100%">
						<fo:table-column column-width="4.75cm"/>
						<fo:table-column column-width="4.75cm"/>
						<fo:table-column column-width="4.75cm"/>
						<fo:table-column column-width="4.75cm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell><fo:block>Based on:<fo:inline font-weight="bold" background-color="{BGC_BUYART}"><xsl:value-of select="basedOn"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell><fo:block>ETD:<fo:inline font-weight="bold" background-color="{BGC_RSVETD}"><xsl:value-of select="estimatedDeliveryDateFormatted"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell><fo:block>Date:<fo:inline font-weight="bold" background-color="{BGC_default}"><xsl:value-of select="creationDateFormatted"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell><fo:block>Week:<fo:inline font-weight="bold" background-color="{BGC_default}"><xsl:value-of select="creationWeek"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell><fo:block>Coll. manager:<fo:inline font-weight="bold" background-color="{BGC_COLMGR}"><xsl:value-of select="collectionManager"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell><fo:block>Prod. manager:<fo:inline font-weight="bold" background-color="{BGC_PRDMGR}"><xsl:value-of select="productManager"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell><fo:block>Designer:<fo:inline font-weight="bold" background-color="{BGC_DESIGNER}"><xsl:value-of select="designer"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell><fo:block>Junior:<fo:inline font-weight="bold" background-color="{BGC_JUNIOR}"><xsl:value-of select="juniorDesigner"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					
					<fo:table table-layout="fixed" width="100%">
						<fo:table-column column-width="30mm"/>
						<fo:table-column column-width="70mm"/>
						<fo:table-column column-width="40mm"/>
						<fo:table-column column-width="50mm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell number-columns-spanned="1"><fo:block border="1pt solid #888888" border-right="0" margin-top="10pt" padding-left="5pt" font-weight="bold" >FACTORY</fo:block></fo:table-cell>
								<fo:table-cell number-columns-spanned="2"><fo:block border="1pt solid #888888" border-left="0" margin-top="10pt" padding-left="5pt"><fo:inline font-weight="bold" background-color="{BGC_SAMPLER}"><xsl:value-of select="sampleRoom"/> <fo:leader leader-length="3mm"/> <xsl:value-of select="sampleRoomName"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell number-rows-spanned="13"><fo:block>
								<xsl:if test="documents/element">
									<fo:table table-layout="fixed" width="46mm" border="1pt solid #888888" margin-left="10pt" margin-top="10pt">
										<fo:table-column />
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell><fo:block padding="5pt" font-weight="bold">Input</fo:block></fo:table-cell>
											</fo:table-row>
											
											<xsl:for-each select="documents/element">
												<fo:table-row>
													<fo:table-cell>
														<fo:block padding="5pt">
															<fo:external-graphic border="1pt solid black" src="{../../fullFolder}/{name}" content-width="scale-to-fit"  width="36mm"/>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:for-each>
										</fo:table-body>
									</fo:table>
								</xsl:if>				
								</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>								
								<fo:table-cell number-columns-spanned="3"><fo:block border="1pt solid #888888" margin-top="10pt" padding-left="5pt" font-weight="bold">MATERIALS</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block margin-top="5pt" padding-left="5pt" >Upper</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="2"><fo:block margin-top="5pt" padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_MAT_UPPER}"><xsl:value-of select="upper"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Lining</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="2"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_MAT_LINING}"><xsl:value-of select="lining"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Sock</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="2"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_MAT_SOCKLINING}"><xsl:value-of select="sockLining"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Outsole</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="2"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_MAT_OUTSOLE}"><xsl:value-of select="outsole"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>								
								<fo:table-cell number-columns-spanned="3"><fo:block border="1pt solid #888888" margin-top="10pt" padding-left="5pt" font-weight="bold">SPECS</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block margin-top="5pt" padding-left="5pt" >Last</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="1"><fo:block margin-top="5pt" padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_LAST}"><xsl:value-of select="last"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell number-columns-spanned="1"><fo:block margin-top="5pt" padding-left="5pt" >
									<xsl:choose>
										<xsl:when test="sendOriginalSamples = 'true'">
											<fo:inline font-weight="bold" background-color="{BGC_ORGSAMSND}"> √ </fo:inline>
										</xsl:when>	
										<xsl:otherwise>
											<fo:inline font-weight="bold" background-color="{BGC_ORGSAMSND}"> × </fo:inline>
										</xsl:otherwise>
									</xsl:choose>
									<fo:inline>Send original sample</fo:inline>
								</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Mould</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_MOULD}"><xsl:value-of select="mould"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >
									<xsl:choose>
										<xsl:when test="hasVisualSupport = 'true'">
											<fo:inline font-weight="bold" background-color="{BGC_default}"> √ </fo:inline>
										</xsl:when>	
										<xsl:otherwise>
											<fo:inline font-weight="bold" background-color="{BGC_default}"> × </fo:inline>
										</xsl:otherwise>
									</xsl:choose>
									<fo:inline>Visual support</fo:inline>
								</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Heel</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="2"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_HEEL}"><xsl:value-of select="heel"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Logo</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="2"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_LOGO}"><xsl:value-of select="logoObject/description"/></fo:inline></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Sample size</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_SAMSIZE}"><xsl:value-of select="sampleSize"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >
									<xsl:choose>
										<xsl:when test="backCounter = 'true'">
											<fo:inline font-weight="bold" background-color="{BGC_BACKCOUNT}"> √ </fo:inline>
										</xsl:when>	
										<xsl:otherwise>
											<fo:inline font-weight="bold" background-color="{BGC_BACKCOUNT}"> × </fo:inline>
										</xsl:otherwise>
									</xsl:choose>
									<fo:inline>Back counter</fo:inline>
								</fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row>											
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >Remarks</fo:block></fo:table-cell>					
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" ><fo:inline font-weight="bold" background-color="{BGC_REMARKS}"><xsl:value-of select="remarks"/></fo:inline></fo:block></fo:table-cell>
								<fo:table-cell number-columns-spanned="1"><fo:block padding-left="5pt" >
									<xsl:choose>
										<xsl:when test="toeBox = 'true'">
											<fo:inline font-weight="bold" background-color="{BGC_TOEBOX}"> √ </fo:inline>
										</xsl:when>	
										<xsl:otherwise>
											<fo:inline font-weight="bold" background-color="{BGC_TOEBOX}"> × </fo:inline>
										</xsl:otherwise>
									</xsl:choose>
									<fo:inline>Toe box</fo:inline>
								</fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>

					<xsl:if test="articleColors/*">
						<fo:table table-layout="fixed" width="100%" >
							<fo:table-column column-width="8mm"/>
							<fo:table-column column-width="80mm"/>
							<fo:table-column column-width="25mm"/>
							<fo:table-column column-width="77mm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="2"><fo:block border="1pt solid #888888" margin-top="10pt" padding-left="5pt" font-weight="bold" margin-right="5mm">COLOR DESIGNS</fo:block></fo:table-cell>
									<fo:table-cell number-columns-spanned="2"><fo:block border="1pt solid #888888" margin-top="10pt" padding-left="5pt" font-weight="bold">COLORS</fo:block></fo:table-cell>
								</fo:table-row>
								<xsl:for-each select="articleColors/element">
									<fo:table-row keep-together="always">
										<fo:table-cell><fo:block border="1pt solid black" margin-top="2mm" font-size="6mm" font-weight="bold" background-color="black" color="white" text-align="center" display-align="center" width="8mm" height="8mm"><xsl:value-of select="colorSequence"/></fo:block></fo:table-cell>
										<fo:table-cell>
											<fo:block width="80mm" margin-top="2mm" >
												<fo:external-graphic border="1pt solid black" src="{../../fullFolder}/{articleNumber}-{colorSequence}.JPG" content-width="scale-to-fit"  width="74mm"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block height="6mm" margin-top="2mm">Color Code</fo:block>
											<fo:block height="6mm">Upper</fo:block>
											<fo:block height="6mm">Lining</fo:block>
											<fo:block height="6mm">Sock</fo:block>
											<fo:block height="6mm">Outsole</fo:block>
											<fo:block height="6mm">Heel</fo:block>
											<fo:block height="6mm">Dev. Eval.</fo:block>
											<fo:block height="6mm">Sales. Eval.</fo:block>
											<fo:block height="6mm">Remarks</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block height="6mm" margin-top="2mm"><fo:inline font-weight="bold" background-color="{BGC_COL_default}"><xsl:value-of select="color"/> <xsl:value-of select="simpleColor"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_MATCOL_UPPER}"><xsl:value-of select="upper"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_MATCOL_LINING}"><xsl:value-of select="lining"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_MATCOL_SOCKLINING}"><xsl:value-of select="sockLining"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_MATCOL_OUTSOLE}"><xsl:value-of select="outsole"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_COL_HEEL}"><xsl:value-of select="heel"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_COL_DEVSTAT}" color="{developmentStatusCol}"><xsl:value-of select="developmentStatusDes"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_COL_RDSTAT}" color="{rdStatusCol}"><xsl:value-of select="rdStatusDes"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_COL_SALESSTAT}" color="{salesStatusCol}"><xsl:value-of select="salesStatusDes"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											<fo:block height="6mm"><fo:inline font-weight="bold" background-color="{BGC_COL_COLREMARKS}"><xsl:value-of select="remarks"/><fo:leader leader-length="1mm"/></fo:inline> </fo:block>
											
										</fo:table-cell>
									</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
						</fo:table>
					</xsl:if>
					
					<fo:block page-break-before="always">
				    	<fo:leader leader-pattern="dots" leader-length="21cm"></fo:leader>
				    </fo:block>
					
	        	</fo:flow>
	      	</fo:page-sequence>
	    </fo:root>
  	</xsl:template>
</xsl:stylesheet>
