import React, {useEffect} from "react";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
// import {useParams} from "react-router-dom";
import HtmlForm from "../Forms/HtmlForm";
import {useForm} from "react-hook-form";
import Loading from "../Loading";
import {Alert} from "react-bootstrap";
import { ExtractList, PrepareCheckboxGroup } from "../../utils";

export default function Groups({ pupilGroups }) {
    const mapCallback = PrepareCheckboxGroup('id', 'name');
    // const { pupilId } = useParams();
    const [groups, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            groups: ExtractList(pupilGroups, 'groupList', mapCallback)
        }
    });

    const getGroups = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/groups',
        });
    };

    const onSubmit = data => {
        console.log(data);
    };

    const groupsCheckboxes = ExtractList(groups, 'groupList', mapCallback);
    const fields = [
        {
            id: 'groups',
            type: 'checkbox',
            options: groupsCheckboxes
        }
    ];

    useEffect(() => {
        getGroups();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert> }
            {!loading && !error && (
                <>
                    <h3>Groups</h3>
                    <HtmlForm formProps={methods} fields={fields} submitCallback={onSubmit} submitLabel={"Update Groups"} />
                </>
            )}
        </>
    );
}