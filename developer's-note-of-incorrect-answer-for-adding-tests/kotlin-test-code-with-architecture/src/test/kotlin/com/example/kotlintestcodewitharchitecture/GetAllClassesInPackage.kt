package com.example.kotlintestcodewitharchitecture

import java.io.File
import java.util.jar.JarFile

internal fun getAllClassesInPackage(packageName: String): List<Class<*>> {
    val classLoader = Thread.currentThread().contextClassLoader
    val path = packageName.replace('.', '/')
    val resources = classLoader.getResources(path)

    val classes = mutableListOf<Class<*>>()
    while (resources.hasMoreElements()) {
        val resource = resources.nextElement()
        if (resource.protocol == "file") {
            val file = File(resource.toURI())
            classes.addAll(findClasses(file, packageName))
        } else if (resource.protocol == "jar") {
            val file = resource.path

            /**
             * file:/path/to/jar/file.jar!/path 일 때 실제 jar file의 경로인 /path/to/jar/file.jar만 추출
             */
            val jarPath = file.substring(5, file.indexOf("!"))
            val jarFile = JarFile(jarPath)
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.name.endsWith(".class") && entry.name.startsWith(path)) {
                    val className = entry.name.replace('/', '.').removeSuffix(".class")
                    classes.add(Class.forName(className))
                }
            }
        }
    }

    return classes
}

private fun findClasses(directory: File, packageName: String): List<Class<*>> {
    val classes = mutableListOf<Class<*>>()
    if (!directory.exists()) {
        return classes
    }

    val files = directory.listFiles()
    for (file in files) {
        if (file.isDirectory) {
            val packageNameNext = "$packageName.${file.name}"
            classes.addAll(findClasses(file, packageNameNext))
        } else if (file.name.endsWith(".class")) {
            val className = "$packageName.${file.name.removeSuffix(".class")}"
            classes.add(Class.forName(className))
        }
    }

    return classes
}
