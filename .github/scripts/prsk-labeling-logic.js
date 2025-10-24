import {
  prskCharacter,
  vocaloidCharacter,
  collaborationScenarios,
  replaceTemplate,
} from './prsk-yell-label.constants'

// Ramdom select prsk character
export const selectPrskCharacter = () => {
  const length = Object.keys(prskCharacter).length
  const randomId = Math.floor(Math.random() * length) + 1
  return prskCharacter[randomId]
}

// Ramdom select vocaloid character
export const selectVocaloidCharacter = () => {
  const length = Object.keys(vocaloidCharacter).length
  const randomId = Math.floor(Math.random() * length) + 1
  return vocaloidCharacter[randomId]
}

// Judge encounter
export const isEncounter = (probability = 0.25) => {
  return Math.random() < probability
}

// create label text
export const createLabelText = (character) => {
  return `${character.icon} | ${character.name}`
}

// Collaboration comment
export const createCollaborationComment = (mainChar, guestChar, prAuthor) => {
  const length = collaborationScenarios.length
  const scenario = collaborationScenarios[Math.floor(Math.random() * length)]

  const storyText = replaceTemplate(scenario.story, {
    main: mainChar.name,
    guest: guestChar.name,
    prAuthor: prAuthor,
  })

  const mainComment = replaceTemplate(mainChar.comment, { prAuthor: prAuthor })
  const guestComment = replaceTemplate(guestChar.comment, { prAuthor: prAuthor })

  return `## ${scenario.title}\n\n${storyText}\n\n---\n### 🎸 素敵な出会いに\n\n **${mainChar.name}**\n\n${mainComment}\n\n**${guestChar.name}**\n\n${guestComment}\n\n> 2人にはたくさんの元気をもらったな✨ ー${getToday()}ー`
}

// Single comment
export function createSingleComment(character, prAuthor) {
  const comment = replaceTemplate(character.comment, { prAuthor: prAuthor })

  return `🎵 **${character.name}** が会いに来てくれた✨\n\n${comment}\n\n> (￣△￣*) .｡oO( 今日も最高な一日だな ー${getToday()}ー`
}

// Create label or obtain existing label
export async function ensureLabel(github, context, labelName, description, color) {
  try {
    await github.rest.issues.getLabel({
      owner: context.repo.owner,
      repo: context.repo.repo,
      name: labelName,
    })
    console.log(`Label already exists: ${labelName}`)
  } catch (error) {
    if (error.status === 404) {
      await github.rest.issues.createLabel({
        owner: context.repo.owner,
        repo: context.repo.repo,
        name: labelName,
        description: description,
        color: color,
      })
      console.log(`Created label: ${labelName}`)
    } else {
      throw error
    }
  }
}

// Add label to PR
export async function addLabels(github, context, labels) {
  await github.rest.issues.addLabels({
    owner: context.repo.owner,
    repo: context.repo.repo,
    issue_number: context.issue.number,
    labels: labels,
  })
  console.log(`Added labels: ${labels.join(', ')} to PR #${context.issue.number}`)
}

// Post comment to PR
export async function postComment(github, context, body) {
  await github.rest.issues.createComment({
    owner: context.repo.owner,
    repo: context.repo.repo,
    issue_number: context.issue.number,
    body: body,
  })
  console.log(`Posted comment to PR #${context.issue.number}`)
}

const getToday = () => {
  const today = new Date()
  const jstDate = new Date(today.toLocaleString('en-US', { timeZone: 'Asia/Tokyo' }))

  const month = (jstDate.getMonth() + 1).toString()
  const day = jstDate.getDate().toString()
  const mm = month.padStart(2, '0')
  const dd = day.padStart(2, '0')

  return `${mm}/${dd}`
}

/**
 * Main logic to handle PR labeling and commenting
 */
export const handlePrLabeling = async (github, context) => {
  const prAuthor = context.payload.pull_request.user.login

  // Select random prsk character
  const selectedPrskChar = selectPrskCharacter()
  const prskLabelName = createLabelText(selectedPrskChar)
  const prskLabelNameComment = replaceTemplate(selectedPrskChar.comment, {
    prAuthor: prAuthor,
  })

  let labelsToAdd = [prskLabelName]
  let commentBody = ''

  // Judge encounter event
  const hasEncounter = isEncounter()
  console.log(`Vocaloid encounter occurred: ${hasEncounter}`)

  if (hasEncounter) {
    // Select random vocaloid character
    const selectedVocaloid = selectVocaloidCharacter()
    const vocaloidLabelName = createLabelText(selectedVocaloid)
    const vocaloidComment = replaceTemplate(selectedVocaloid.comment, { prAuthor: prAuthor })

    labelsToAdd.push(vocaloidLabelName)

    // Create collaboration comment
    commentBody = createCollaborationComment(selectedPrskChar, selectedVocaloid, prAuthor)

    // Create vocaloid label
    await ensureLabel(
      github,
      context,
      vocaloidLabelName,
      vocaloidComment,
      selectedVocaloid.color,
    )
  } else {
    // Create single comment
    commentBody = createSingleComment(selectedPrskChar, prAuthor)
  }

  // Create prsk label
  await ensureLabel(
    github,
    context,
    prskLabelName,
    prskLabelNameComment,
    selectedPrskChar.color,
  )

  // Add labels to PR
  await addLabels(github, context, labelsToAdd)

  // Post comment to PR
  await postComment(github, context, commentBody)
}
