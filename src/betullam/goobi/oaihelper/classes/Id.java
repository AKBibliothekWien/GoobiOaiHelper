/**
 * This file is part of GoobiOaiHelper.
 * 
 * GoobiOaiHelper is free software: you can redistribute it and/or modify
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GoobiOaiHelper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GoobiOaiHelper.  If not, see <http://www.gnu.org/licenses/>.
 */

package betullam.goobi.oaihelper.classes;

import java.util.List;

/**
 * This class holds the identifiers which are relevant for further processing.
 * 
 * @author Michael Birkner
 */

public class Id {

	private String dmdlogId;
	private String logId;
	private List<String> physIds;
	
	public Id(String dmdlogId, String logId, List<String> physIds) {
		this.dmdlogId = dmdlogId;
		this.logId = logId;
		this.physIds = physIds;
	}
	
	public String getDmdlogId() {
		return dmdlogId;
	}
	public void setDmdlogId(String dmdlogId) {
		this.dmdlogId = dmdlogId;
	}
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public List<String> getPhysIds() {
		return physIds;
	}
	public void setPhysIds(List<String> physIds) {
		this.physIds = physIds;
	}

	@Override
	public String toString() {
		return "Id [dmdlogId=" + dmdlogId + ", logId=" + logId + ", physIds=" + physIds + "]";
	}
	
	
	
}
