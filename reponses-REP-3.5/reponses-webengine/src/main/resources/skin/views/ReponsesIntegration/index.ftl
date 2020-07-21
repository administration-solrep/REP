<@extends src="base.ftl">

<@block name="title">Welcome to Nuxeo WebEngine!</@block>

<@block name="header"><h1><a href="${appPath}">Nuxeo WebEngine</a></h1></@block>

<@block name="content">
Hello <strong>${Context.principal.name}</strong>!.
</p>
<div id="mainContentBox">
	<form action="${This.path}/fromFrom" method="POST" enctype="multipart/form-data">
		<table class="formFill">
		    <tbody>
		        <tr>
		        	<td>File : </td>
			        <td><input type="file" name="file" ></td>
		        </tr>
		        <tr>
		          <td colspan="2" align="right"><input type="submit" value="Save"/></td>
		        </tr>
		    </tbody>
		</table>		
	</form>
</div>
</@block>
</@extends>
