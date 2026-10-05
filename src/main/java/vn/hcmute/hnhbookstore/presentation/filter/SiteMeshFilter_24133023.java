package vn.hcmute.hnhbookstore.presentation.filter;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

public final class SiteMeshFilter_24133023 extends ConfigurableSiteMeshFilter {
    @Override protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.setDecoratorPrefix("")
            .addDecoratorPath("/*", "/WEB-INF/decorators/user.jsp")
            .addDecoratorPath("/admin", "/WEB-INF/decorators/admin.jsp")
            .addDecoratorPath("/admin/*", "/WEB-INF/decorators/admin.jsp")
            .addExcludedPath("/assets/*");
    }
}
