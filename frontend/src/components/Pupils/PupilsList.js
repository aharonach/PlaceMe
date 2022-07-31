import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert} from "react-bootstrap";
import { ExtractList } from "../../utils";

function PupilsList() {
    const [pupils, error, loading, axiosFetch] = useAxios();

    const columns = {
        id: "ID",
        givenId: "Given ID",
        firstName: "First Name",
        lastName: "Last Name",
        gender: "Gender",
        birthDate: "Birth Date",
        createdTime: "Created Time"
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/pupils',
        });
    };

    useEffect(() => {
        getData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupils && <TableList basePath="/pupils/" items={ExtractList(pupils, 'pupilList')} columns={columns} />}
        </>
    )
}

export default PupilsList;