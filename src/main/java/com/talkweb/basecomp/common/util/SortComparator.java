package com.talkweb.basecomp.common.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("unchecked")
public class SortComparator implements Comparator<JSONObject> {
	List<Field> sortField = new ArrayList<>();

	public SortComparator(String sortField) {
		this.sortField.add(new Field(sortField));
	}
	
	public SortComparator(Field... sortField) {
		for (Field f: sortField)
			this.sortField.add(f);
	}

	public SortComparator(String... sortField) {
		for (String f: sortField)
			this.sortField.add(new Field(f));
	}

	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		if (o1 == o2)
			return 0;
		for (Field f: this.sortField) {
			Object obj1 = f.getValue(o1);
			Object obj2 = f.getValue(o2);
			boolean isempty1 = StringUtils.isEmpty(obj1);
			boolean isempty2 = StringUtils.isEmpty(obj2);
			if (obj1 == obj2 || isempty1 && isempty2)
				continue;
			
			if (isempty1) //空值往最后移
				return 1;

			if (isempty2)
				return -1;
			
			if (obj1.equals(obj2))
				continue;
			
			int ret = 0;
			if (f.type == FIELDTYPE_CHINESE) {
				ret = chinaComparator.compare(obj1, obj2);
			} else if (obj1 instanceof Comparable){
				ret = ((Comparable<Object>) obj1).compareTo(obj2);
			}
			if (ret == 0)
				continue;
			
			return f.desc ? -ret : ret;
		}
		return 0;
	}
	
	public static Field getField(String name, int type) {
		return new Field(name, type);
	}
	
	private final static Comparator<Object> chinaComparator = Collator.getInstance(java.util.Locale.CHINA);
	
	public final static int FIELDTYPE_INT = 1;
	public final static int FIELDTYPE_LONG = 2;
	public final static int FIELDTYPE_LETTER = 3;
	public final static int FIELDTYPE_CHINESE = 4;

	public final static Field teacherSortF = new Field("teacherName", FIELDTYPE_CHINESE);
	public final static Field textSortF = new Field("text", FIELDTYPE_CHINESE);
	public final static Field classSortF = new Field("className", FIELDTYPE_LETTER);
	public final static Field nameSortF = new Field("name", FIELDTYPE_LETTER);
	public final static Field gradeSortF = new Field("usedGrade", FIELDTYPE_INT, true);
	public final static Field lessonSortF = new Field("id", FIELDTYPE_INT);
	public final static Field typeSortF = new Field("type", FIELDTYPE_INT);
	public final static Field valueSortF = new Field("value", FIELDTYPE_LONG);

	public final static Comparator<JSONObject> classSort = new SortComparator(classSortF);
	public final static Comparator<JSONObject> textSort = new SortComparator(textSortF);
	public final static Comparator<JSONObject> nameSort = new SortComparator(nameSortF);
	public final static Comparator<JSONObject> gradeSort = new SortComparator(gradeSortF);
	public final static Comparator<JSONObject> lessonSort = new SortComparator(lessonSortF);
	public final static Comparator<JSONObject> valueSort = new SortComparator(valueSortF);
	public final static Comparator<JSONObject> typeValueSort = new SortComparator(typeSortF, valueSortF);
	
	public final static class Field {
		public String name;
		public int type;
		public boolean desc;

		public Field(String name) {
			this(name, FIELDTYPE_LETTER, false);
		}
		
		public Field(String name, int type) {
			this(name, type, false);
		}
		
		public Field(String name, int type, boolean desc) {
			this.name = name;
			this.type = type;
			this.desc = desc;
		}
		
		public Object getValue(JSONObject obj) {
			switch(this.type) {
			case FIELDTYPE_INT:
				return obj.getInteger(name);
			case FIELDTYPE_LONG:
				return obj.getLong(name);
			}
			return obj.getString(name);
		}
    }
}
