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

package classes;

import java.util.List;

/**
 * This class holds information of structure elements (e. g. "Article", "Chapter", etc.)
 * 
 * @author Michael Birkner
 */

public class StructureElement {

	private Id id;
	private String structureElementType;
	private String title;
	private String subTitle;
	private List<String> authors;
	private String artAbstract;
	private String language;
	private String pageLabel;
	
	public StructureElement(Id id, String structureElementType, String title, String subTitle, List<String> authors, String artAbstract, String language, String pageLabel) {
		this.id = id;
		this.structureElementType = structureElementType;
		this.title = title;
		this.subTitle = subTitle;
		this.authors = authors;
		this.artAbstract = artAbstract;
		this.language = language;
		this.pageLabel = pageLabel;
	}

	
	public Id getId() {
		return id;
	}
	public void setId(Id id) {
		this.id = id;
	}
	public String getStructureElementType() {
		return structureElementType;
	}
	public void setStructureElementType(String structureElementType) {
		this.structureElementType = structureElementType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public List<String> getAuthors() {
		return authors;
	}
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
	public String getArtAbstract() {
		return artAbstract;
	}
	public void setArtAbstract(String artAbstract) {
		this.artAbstract = artAbstract;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPageLabel() {
		return pageLabel;
	}
	public void setPageLabel(String pageLabel) {
		this.pageLabel = pageLabel;
	}
}
