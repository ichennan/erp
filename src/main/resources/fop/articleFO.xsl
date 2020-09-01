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
  	<xsl:template match="array">
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial" font-size="10pt">
	      	<fo:layout-master-set>
        		<fo:simple-page-master master-name="simpleA4" 
        							   page-height="29.7cm" 
        							   page-width="21cm" 
        							   margin-top="5mm" 
        							   margin-bottom="0mm" 
        							   margin-left="1cm" 
        							   margin-right="1cm">
          			<fo:region-body region-name="content" margin-top="0mm" margin-bottom="10mm"/>
					<fo:region-after region-name="footer" extent="10mm"/>          			
        		</fo:simple-page-master>
      		</fo:layout-master-set>

	      	<fo:page-sequence master-reference="simpleA4">
				<fo:static-content flow-name="footer">
					<fo:block> test test </fo:block>
				</fo:static-content> 	      	

	      	
	        	<fo:flow flow-name="content" >
					<fo:block> test test </fo:block>
	        	</fo:flow>
	      	</fo:page-sequence>
	    </fo:root>
  	</xsl:template>
</xsl:stylesheet>
