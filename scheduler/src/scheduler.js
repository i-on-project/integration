'use strict'

import yaml from 'js-yaml'
import fs from 'fs'
import path, {dirname} from 'path'
import {fileURLToPath} from 'url'
import {buildCalendarRequest, buildTimetableRequest, buildEvaluationRequest} from "./integrationApi.js";


try {
    const [uri, token, format, filePath] = process.argv.slice(2)

    const __filename = fileURLToPath(import.meta.url);
    const __dirname = dirname(__filename);

    const pathToFile = path.resolve(__dirname, filePath)
    let fileContents = fs.readFileSync(pathToFile, 'utf8')
    let data = yaml.load(fileContents)

    const institutions = getInstitutions(data)

    const params = {token, format, uri}

    institutions.flatMap(i => {
        const calRequest = buildCalendarRequest(params, i.id)

        const ttRequests = i.programmes.map(p => buildTimetableRequest(params, i.id, p))
        const esRequests = i.programmes.map(p => buildEvaluationRequest(params, i.id, p))

        return ttRequests.concat(esRequests, calRequest)
    }).forEach(p => {
        p.then(res => res.json())
            .then(r => console.log(r))
            .catch(e => console.error(`Error: ${e}`))
    })

} catch (e) {
    console.error(e)
}

function acronym(text) {
    return text.split(/[\s_-]+/)
        .filter(s => s.length > 3)
        .map(c => c[0].toUpperCase())
        .join('');
}

function getInstitutions(data) {
    return data.map(institution => {
        const id = institution.identifier
        const programmes = institution.programmes.map(programme => acronym(programme.name))

        return {id, programmes}
    })
}


