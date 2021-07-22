'use strict'

import fetch from "node-fetch";

const buildRequest = (params, jobType, institution, programme) => {
    const headers = getHeaders(params.token)
    const body = getRequestBody(jobType, params.format, institution, programme)

    const options = {
        method: 'POST',
        body: body,
        headers: headers
    }

    return fetch(params.uri, options)
}

const getRequestBody = (type, format, institution, programme) => {
    const body = {institution, programme, format, type}
    return JSON.stringify(body)
}

const getHeaders = (token) => {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    }
}

export const buildCalendarRequest = (params, institution) => buildRequest(params, 'calendar', institution)
export const buildTimetableRequest = (params, institution, programme) => buildRequest(params, 'timetable', institution, programme)
export const buildEvaluationRequest = (params, institution, programme) => buildRequest(params, 'evaluations', institution, programme)