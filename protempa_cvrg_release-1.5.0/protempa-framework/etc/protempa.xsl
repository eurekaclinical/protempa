<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">
 
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
 
  <xsl:template match="/patients">
    <html>
      <head> <title>PROTEMPA Output</title> </head>
      <body>
        <h1>PROTEMPA Output</h1>
        <ul>
          <xsl:apply-templates select="patient"/>
        </ul>
      </body>
    </html>
  </xsl:template>
 
  <xsl:template match="patient">
    <li>
      <xsl:apply-templates
      select="proposition[@id='PatientAll']/references/reference[@name='patientDetails']/proposition[@id='Patient']"/>
    </li>
  </xsl:template>

  <xsl:template
  match="proposition[@id='PatientAll']/references/reference[@name='patientDetails']/proposition[@id='Patient']">
    <xsl:value-of select="properties/property[@name='lastName']/valueDisplayName"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="properties/property[@name='firstName']/valueDisplayName"/>
    <xsl:text> (EMPI=</xsl:text>
    <xsl:value-of select="properties/property[@name='empiId']/valueDisplayName"/>
      <xsl:text>)</xsl:text>
    <ul>
      <xsl:apply-templates
      select="references/reference[@name='encounters']/proposition[@id='Encounter']"/>
    </ul>
  </xsl:template>

  <xsl:template
    match="references/reference[@name='encounters']/proposition[@id='Encounter']">
    <li>
      <xsl:value-of select="properties/property[@name='type']/valueDisplayName"/>
      <xsl:text> Encounter at </xsl:text>
      <xsl:value-of
    select="properties/property[@name='healthcareEntity']/valueDisplayName"/>
      <xsl:text> </xsl:text>
      <xsl:value-of
    select="properties/property[@name='organization']/valueDisplayName"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="properties/property[@name='dischargeUnit']/valueDisplayName"/>
      <xsl:text> on </xsl:text>
      <xsl:value-of select="start"/><xsl:text> to </xsl:text><xsl:value-of select="end"/>
      <ul>
        <li>
	  <xsl:text>Provider: </xsl:text>
          <xsl:value-of select="references/reference[@name='provider']/proposition[@id='AttendingPhysician']/properties/property[@name='fullName']/valueDisplayName"/>
	</li>
        <li>
	  <xsl:text>Diagnoses</xsl:text>
          <ul>
            <xsl:apply-templates select="references/reference[@name='diagnosisCodes']/proposition"/>
          </ul>
	</li>
        <li>
	  <xsl:text>Procedures</xsl:text>
          <ul>
            <xsl:apply-templates select="references/reference[@name='procedures']/proposition"/>
          </ul>
	</li>
        <li>
	  <xsl:text>Medication Orders</xsl:text>
          <ul>
            <xsl:apply-templates select="references/reference[@name='medicationHistory']/proposition"/>
          </ul>
	</li>
        <li>
	  <xsl:text>Labs</xsl:text>
          <ul>
            <xsl:apply-templates select="references/reference[@name='labs']/proposition"/>
          </ul>
	</li>
<li>
	  <xsl:text>Vitals</xsl:text>
          <ul>
            <xsl:apply-templates select="references/reference[@name='vitals']/proposition"/>
          </ul>
	</li>
        <li>
          <xsl:text>Discharged to </xsl:text>
          <xsl:value-of
    select="properties/property[@name='dischargeDisposition']/valueDisplayName"/>
        </li>
      </ul>
    </li>
  </xsl:template>

  <xsl:template match="references/reference[@name='diagnosisCodes']/proposition">
    <li>
      <xsl:value-of select="displayName"/>
      <xsl:text> (</xsl:text>
      <xsl:value-of select="properties/property[@name='code']/valueDisplayName"/>
      <xsl:text>)</xsl:text>
    </li>
  </xsl:template>

  <xsl:template match="references/reference[@name='procedures']/proposition">
    <li>
      <xsl:value-of select="displayName"/>
      <xsl:text> (</xsl:text>
      <xsl:value-of select="properties/property[@name='code']/valueDisplayName"/>
      <xsl:text>) on </xsl:text>
      <xsl:value-of select="start"/>
    </li>
  </xsl:template>

  <xsl:template match="references/reference[@name='medicationHistory']/proposition">
    <li>
      <xsl:value-of select="properties/property[@name='orderDescription']/valueDisplayName"/>
      <xsl:text> (</xsl:text>
      <xsl:value-of
      select="properties/property[@name='orderContext']/valueDisplayName"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="properties/property[@name='orderStatus']/valueDisplayName"/>
      <xsl:text>) on </xsl:text>
      <xsl:value-of select="start"/>
    </li>
  </xsl:template>

  <xsl:template match="references/reference[@name='labs']/proposition">
    <li>
      <xsl:value-of select="displayName"/>
      <xsl:text> on </xsl:text>
      <xsl:value-of select="start"/>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="value"/>
      <xsl:text> </xsl:text>
      <xsl:variable name="unitOfMeasure" select="properties/property[@name='unitOfMeasure']/valueDisplayName"/>
      <xsl:value-of select="$unitOfMeasure"/>
      <xsl:text> (Interpretation: </xsl:text>
      <xsl:value-of
      select="properties/property[@name='interpretation']/valueDisplayName"/>
      <xsl:text>, Range: </xsl:text>
      <xsl:value-of
      select="properties/property[@name='referenceRangeLow']/valueDisplayName"/>
      <xsl:text> &#8211; </xsl:text>
      <xsl:value-of
      select="properties/property[@name='referenceRangeHigh']/valueDisplayName"/>
      <xsl:text> </xsl:text>
      <xsl:copy-of select="$unitOfMeasure" />
      <xsl:text>)</xsl:text>
    </li>
  </xsl:template>

  <xsl:template match="references/reference[@name='vitals']/proposition">
    <li>
      <xsl:value-of select="displayName"/>
      <xsl:text> on </xsl:text>
      <xsl:value-of select="start"/>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="value"/>
    </li>
  </xsl:template>

</xsl:stylesheet>