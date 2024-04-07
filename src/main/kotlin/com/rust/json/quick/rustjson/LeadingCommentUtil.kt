package com.rust.json.quick.rustjson


/**
 * Comment Template
 */
const val LEADING_COMMENT = """ 
    /// 
    ///
    /// fn main() {
    ///     let json = r#"{{JSON}}"#;
    ///     let model: {{StructName}} = serde_json::from_str(&json).unwrap();
    /// }

"""

object LeadingCommentUtil {
    fun getLeadingComment(json: String, structName: String): String {

        return "";
    }
}