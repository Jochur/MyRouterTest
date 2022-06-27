package com.grechur.buried;

import com.android.build.api.instrumentation.AsmClassVisitorFactory;
import com.android.build.api.instrumentation.ClassContext;
import com.android.build.api.instrumentation.ClassData;
import com.android.build.api.instrumentation.FramesComputationMode;
import com.android.build.api.instrumentation.InstrumentationParameters;
import com.android.build.api.instrumentation.InstrumentationScope;
import com.android.build.api.variant.AndroidComponentsExtension;
import com.android.build.api.variant.Component;
import com.android.build.api.variant.Instrumentation;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import javax.inject.Inject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ActivityPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        AndroidComponentsExtension androidComponentsExtension = project.getExtensions().getByType(AndroidComponentsExtension.class);
        androidComponentsExtension.onVariants(androidComponentsExtension.selector().all(), new Action<Component>() {
            @Override
            public void execute(Component component) {
                Instrumentation instrumentation = component.getInstrumentation();
                instrumentation.transformClassesWith(ActivityClassVisitorFactory.class,
                        InstrumentationScope.ALL, new Function1<ActivityParam, Unit>() {
                            @Override
                            public Unit invoke(ActivityParam activityParam) {
                                return null;
                            }
                        });
                instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES);
            }
        });
    }

    interface ActivityParam extends InstrumentationParameters {
    }

    abstract static class ActivityClassVisitorFactory implements AsmClassVisitorFactory<ActivityParam> {
        @Inject
        public ActivityClassVisitorFactory() {

        }

        @NotNull
        @Override
        public ClassVisitor createClassVisitor(@NotNull ClassContext classContext, @NotNull ClassVisitor classVisitor) {
            return new ActivityVisitor(Opcodes.ASM7,classVisitor);
        }

        @Override
        public boolean isInstrumentable(@NotNull ClassData classData) {
            return classData.getClassName().startsWith("com.grechur");
        }
    }
}
