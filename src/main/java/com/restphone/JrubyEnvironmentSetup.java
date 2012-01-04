package com.restphone;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;


public class JrubyEnvironmentSetup extends RubyObject  {
    private static final Ruby __ruby__ = Ruby.getGlobalRuntime();
    private static final RubyClass __metaclass__;

    static {
        String source = new StringBuilder("require 'java'\n" +
            "\n" +
            "java_package 'com.restphone'\n" +
            "\n" +
            "class JrubyEnvironmentSetup\n" +
            "  java_signature 'void addToLoadPath(String file)'\n" +
            "  def self.add_to_load_path file\n" +
            "    $LOAD_PATH << file\n" +
            "  end\n" +
            "end\n" +
            "\n" +
            "").toString();
        __ruby__.executeScript(source, "src/main/jruby/jruby_environment_setup.rb");
        RubyClass metaclass = __ruby__.getClass("JrubyEnvironmentSetup");
        metaclass.setRubyStaticAllocator(JrubyEnvironmentSetup.class);
        if (metaclass == null) throw new NoClassDefFoundError("Could not load Ruby class: JrubyEnvironmentSetup");
        __metaclass__ = metaclass;
    }

    /**
     * Standard Ruby object constructor, for construction-from-Ruby purposes.
     * Generally not for user consumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    private JrubyEnvironmentSetup(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    /**
     * A static method used by JRuby for allocating instances of this object
     * from Ruby. Generally not for user comsumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    public static IRubyObject __allocate__(Ruby ruby, RubyClass metaClass) {
        return new JrubyEnvironmentSetup(ruby, metaClass);
    }
        
    /**
     * Default constructor. Invokes this(Ruby, RubyClass) with the classloader-static
     * Ruby and RubyClass instances assocated with this class, and then invokes the
     * no-argument 'initialize' method in Ruby.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    public JrubyEnvironmentSetup() {
        this(__ruby__, __metaclass__);
        RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "initialize");
    }

    
    public static void addToLoadPath(String file) {
        IRubyObject ruby_file = JavaUtil.convertJavaToRuby(__ruby__, file);
        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), __metaclass__, "add_to_load_path", ruby_file);
        return;

    }

}
