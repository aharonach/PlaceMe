import React, {useEffect} from 'react';
import {useParams} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import { LinkContainer } from 'react-router-bootstrap'


function PupilProfile() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useAxios();

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/pupils/${pupilId}`,
        });
    }

    const handleDelete = () => {
        console.log( pupil.id );
    }

    useEffect(() => getData(), []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">Error</Alert>}
            {!loading && !error && pupil &&
                <article className="pupil">
                    <LinkContainer to="edit"><Button variant="secondary">Edit</Button></LinkContainer>
                    <Button variant="danger" onClick={handleDelete}>Delete</Button>
                    <h2>{pupil.firstName} {pupil.lastName}</h2>
                    {Object.keys(pupil).map(key => <p key={key}>{'object' === typeof (pupil[key]) ? '' : pupil[key]}</p>)}
                </article>
            }
        </>
    )
}

export default PupilProfile;