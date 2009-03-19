<?xml version="1.0" encoding="ISO-8859-1"?>
<helpset>
  <title>City of Heroes Demo Editor</title>
  <maps>
    <homeID>about</homeID>
    <mapref location="Map.jhm"/>
  </maps>
  <view mergetype="javax.help.UniteAppendMerge">
    <name>TOC</name>
    <label>Table of Contents</label>
    <type>javax.help.TOCView</type>
    <data>TOC.xml</data>
  </view>
  <view mergetype="javax.help.SortMerge">
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>Index.xml</data>
  </view>
  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
  </view>
  <presentation default="true">
    <name>main window</name>
    <size height="400" width="600"/>
    <location x="200" y="200"/>
    <title>Demo Editor Help</title>
    <toolbar>
      <helpaction>javax.help.BackAction</helpaction>
      <helpaction>javax.help.ForwardAction</helpaction>
      <helpaction>javax.help.HomeAction</helpaction>
    </toolbar>
  </presentation>
</helpset>
