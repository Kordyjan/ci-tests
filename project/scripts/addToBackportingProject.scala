//> using scala 3.3.1
//> using toolkit 0.2.1
//> using lib pro.kordyjan::pytanie:0.1.6

import pytanie.*
import sttp.client4.*

lazy val apiToken =
  System.getenv("GRAPHQL_API_TOKEN")

case class ID(value: String) derives WrapperVariable

// val PROJECT_ID = ID("PVT_kwDOACj3ec4AWSoi")
// val FIELD_ID = ID("PVTF_lADOACj3ec4AWSoizgO7uJ4")

@main def run(commitSha: String) =
  val (id, date) = getPrData(commitSha)
  println((id, date))
  // val newId = addItem(id)
  // timestampItem(newId, date)

def getPrData(commitSha: String): (ID, String) =
  val res = query"""
    |query prForCommit {
    |  repository(owner: "Kordyjan", name: "ci-tests") {
    |    object(expression: "ace504e451b5a2f9f34da47f1cdb084162ffd9bf") {
    |      __typename
    |      ... on Commit {
    |        associatedPullRequests(first: 1) {
    |          nodes {
    |            number
    |            id
    |            mergedAt
    |          }
    |        }
    |        parents(first: 2) {
    |          nodes {
    |            associatedPullRequests(first: 1) {
    |              nodes {
    |                number
    |                id
    |                mergedAt
    |              }
    |            }
    |          }
    |        }
    |      }
    |    }
    |  }
    |}
    """.send(
      uri"https://api.github.com/graphql",
      "DummyUser",
      apiToken
    )
  val pr = res.repository.`object`.asCommit.get.parents.nodes(1).associatedPullRequests.nodes.head
  (ID(pr.id), pr.mergedAt)

// def timestampItem(id: ID, date: String) =
//   query"""
//     |mutation editField {
//     |  updateProjectV2ItemFieldValue(input: {
//     |    projectId: $PROJECT_ID,
//     |    itemId: $id,
//     |    fieldId: $FIELD_ID,
//     |    value: { text: $date }
//     |  }) {
//     |    projectV2Item {
//     |      updatedAt
//     |    }
//     |  }
//     |}
//     """.send(
//       uri"https://api.github.com/graphql",
//       "DummyUser",
//       apiToken
//     )

// def addItem(id: ID) =
//   val res = query"""
//     |mutation addItem {
//     |  addProjectV2ItemById(input: {
//     |    projectId: $PROJECT_ID,
//     |    contentId: $id
//     |  }) {
//     |    item {
//     |      id
//     |    }
//     |  }
//     |}
//     """.send(
//       uri"https://api.github.com/graphql",
//       "DummyUser",
//       apiToken
//     )
//   ID(res.addProjectV2ItemById.item.id)
