<% 
	response.addHeader("Cache-control", "max-age=500");
	try {
		Thread.sleep(100);
	} catch (InterruptedException e) {
		// Nothing to do
	}
%>
<!--$includetemplate$aggregated2$template.html$-->
<!--$beginput$title$--><title>Template exemple</title><!--$endput$-->
<!--$beginput$content$-->
<div style="background-color: yellow">
Some text from aggregated1
</div>
<!--$endput$-->
<!--$endincludetemplate$-->
