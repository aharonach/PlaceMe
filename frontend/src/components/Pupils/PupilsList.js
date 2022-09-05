import React from 'react';
import RecordList from "../RecordList";
import columns from "./columns";
import {humanizeTime} from "../../utils";
import PupilGender from "./PupilGender";

export const mapPupils = (pupil ) => {
    return { ...pupil,
        birthDate: humanizeTime(pupil.birthDate, 'DD/MM/YYYY'),
        gender: <PupilGender pupil={pupil} />
    }
};

export default function PupilsList() {
    return <RecordList
        fetchUrl="/pupils/"
        propertyName="pupilList"
        title={<h1>Pupils</h1>}
        addButton="Add Pupil"
        columns={columns}
        mapCallback={mapPupils}
        linkField="givenId"
        sorting={['firstName', 'lastName', 'givenId', 'createdTime', 'gender', 'birthDate']}
    />
}
