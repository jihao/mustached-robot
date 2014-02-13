package schedule;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(name="nurserostering")
public class NurseRosteringSettings {

    private String dataDir;

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	@Override
	public String toString() {
		return "NurseRosteringSettings [dataDir=" + dataDir + "]";
	}

    

}