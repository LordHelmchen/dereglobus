// log4j configuration
log4j {
	appender.stdout = 'org.apache.log4j.ConsoleAppender'
	appender.'stdout.layout'='org.apache.log4j.PatternLayout'
	appender.'stdout.layout.ConversionPattern'='[%r] %c{2} %m%n'
	appender.errors = 'org.apache.log4j.FileAppender'
	appender.'errors.layout'='org.apache.log4j.PatternLayout'
	appender.'errors.layout.ConversionPattern'='[%r] %c{2} %m%n'
	appender.'errors.File'='stacktrace.log'
	rootLogger='error,stdout'
	logger {
		griffon='error'
		//		griffon.app='debug'
		StackTrace='error,errors'
		org {
			codehaus.griffon.commons='info' // core / classloading
		}
	}
	additivity.StackTrace=false
}

//environments {
//	production {
//		log4j {
//			logger { griffon.app='error' }
//		}
//	}
//}