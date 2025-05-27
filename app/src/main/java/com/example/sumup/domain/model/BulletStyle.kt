package com.example.sumup.domain.model

/**
 * Different bullet styles for different personas
 * Each style affects how AI generates summary bullets
 */
enum class BulletStyle {
    BALANCED,     // Mix of key points and details
    EDUCATIONAL,  // Learning-focused with examples
    ACTIONABLE,   // Business actions and decisions
    PRECISE,      // Legal/formal accuracy
    DETAILED,     // Technical specifications
    CONCEPTUAL    // High-level themes and ideas
}
