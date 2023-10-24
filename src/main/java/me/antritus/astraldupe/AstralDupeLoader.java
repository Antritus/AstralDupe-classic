package me.antritus.astraldupe;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class AstralDupeLoader implements PluginLoader {
	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();
		// Just ignore all of these an add them to the jar file.
		// h2 won't be needeed when databases are converted to MySQL
//		resolver.addDependency(new Dependency(new DefaultArtifact("com.h2database:h2:2.1.214"), null));
//		resolver.addDependency(
//				new Dependency(
//						new DefaultArtifact("org.mariadb.jdbc:mariadb-java-client:3.1.4"), null));
//		resolver.addDependency(
//				new Dependency(
//						new DefaultArtifact("net.dv8tion:JDA:5.0.0-beta.10"), null));
//		mavenLibraryResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-annotations:2.15.2"), null));
//		mavenLibraryResolver.addDependency(new Dependency(new DefaultArtifact(""), null));
//		mavenLibraryResolver.addDependency(new Dependency(new DefaultArtifact(""), null));
//		mavenLibraryResolver.addDependency(new Dependency(new DefaultArtifact(""), null));
		classpathBuilder.addLibrary(resolver);
	}
}
